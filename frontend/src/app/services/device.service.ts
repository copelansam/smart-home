import { Injectable, inject, signal, NgZone, computed } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { WebSocketService } from './websocket.service'
import { SmartDevice, DeviceType, DeviceLog, ITransition } from '../models/device.model'

/**
 * DTO (Data Transfer Object) representing the raw shape of device data
 * received from the backend API / WebSocket.
 *
 * This is intentionally kept separate from the frontend model (SmartDevice)
 * so that backend changes don’t directly break UI logic.
 */
interface DeviceDto{
  uuid: string;
  name: string;
  location: string;
  deviceType: DeviceType;
  state: string;
  isOn: boolean;
  availableTransitions: ITransition[];
  properties: Record<string, any>;
  updatableFields: ITransition[];
  }

  type DeviceEvent =
    | { type: 'UPSERT'; device: DeviceDto }
    | { type: 'RESET'; devices: DeviceDto[] }

@Injectable({
  providedIn: 'root',
})
export class DeviceService {

  // Angular dependency injection
  private http = inject(HttpClient);
  private zone = inject(NgZone);
  private socket = inject(WebSocketService);

  // Base API endpoint for device-related operations
  private apiUrl = 'http://localhost:8080/api/devices';

 /**
   * When the service is created, we immediately start listening
   * for real-time device updates from the WebSocket.
   */
  constructor() {

    // This constructor was generated with the help of ChatGPT

    // Establish WebSocket connections and subscribe to device event streams.
    // Incoming events are normalized and used to keep the local reactive device state
    // in sync with backend changes (UPSERT, RESET, DELETE).

     /**
       * CREATE + UPDATE + STATE CHANGES
       */
     this.socket.deviceUpdates$
       .pipe(
         /**
              * We normalize ALL incoming WebSocket messages into a single
              * predictable "DeviceEvent" shape.
              *
           */
         map((event: unknown): DeviceEvent => {

console.log('🔥 RAW WS EVENT:', event);
           /**
             RESET (array of DTOs)
             Backend sends a full list of devices.

             Convert each device DTO to a SmartDeviceBase and wrap in DeviceEvent
            */
           if (Array.isArray(event)) {
             return {
               type: 'RESET',
               devices: event as DeviceDto[]
             };
           }

           /**
            * UPSERT (single DTO)
            * Backend sends single device

            Convert to SmartDeviceBase and wrap in DeviceEvent
            */
           return {
             type: 'UPSERT',
             device: event as DeviceDto
           };
         })
       )
       .subscribe(event => {
          /**
               * NgZone ensures Angular knows about updates coming from
               * WebSocket (which runs outside Angular’s normal change detection).
               */
         this.zone.run(() => {

           /**
            * Case 1: RESET, replace entire device list with updated devices
            */
           if (event.type === 'RESET') {
             this.devices.set(
               event.devices.map(d => this.mapDtoToModel(d)));
             return;
           }

            /**
             * Case 2: UPSERT, update or insert a single device
             */
           if (event.type === 'UPSERT') {
             const updated = this.mapDtoToModel(event.device);

             this.devices.update(current => {
               const exists = current.some(d => d.uuid === updated.uuid);

               return exists
                 ?
                 // If the device already exists, update it
                 current.map(d =>
                     d.uuid === updated.uuid ? updated : d
                   )
                 :
                 // If the device is new, add it to the list
                 [...current, updated];
             });
           }

         });

       });

     /**
       * DELETE events
       */
    this.socket.deviceDelete$.subscribe(
      (uuid: string) => {

        this.zone.run(() => {
          this.devices.update(current =>
            current.filter(d => d.uuid != uuid)
            );
          });
      });
  }

 /**
   * Static list of supported device types for UI dropdowns/forms.
   */
  getDeviceTypes() {
      return [
        { label: 'Thermostat', value: 'THERMOSTAT' },
        { label: 'Light', value: 'LIGHT' },
        { label: 'Door Lock', value: 'DOORLOCK' },
        { label: 'Fan', value: 'FAN' }
      ];
    }

  /**
     * Reactive state holding all devices.
     * Components can subscribe to this signal to automatically react to updates.
     */
  devices = signal<SmartDevice[]>([]);

  /**
     * Holds logs/history for a selected device.
     */
  currentLogs = signal<DeviceLog[]>([]);

/**
   * Derived state: list of unique locations based on current devices.
   * Automatically recalculates whenever `devices` changes.
   */
  locations = computed(() => {
    const allLocations = this.devices().map(d => d.location);

    // Creates a list of all unique locations and assigns them a label and value so that they can be selected by the user,
    // removes duplicates and formats them for UI dropdown
    return Array.from(new Set(allLocations)).map(loc => ({
      label: loc,
      value: loc
      }));
    });

  /**
   * Fetch devices from backend with optional filters.
   *
   * @param deviceType - filter by device type
   * @param location - filter by location
   * @param isOn - filter by power state
   */
  fetchDevices(deviceType ?: string, location ?: string, isOn ?: boolean){

    let params = new HttpParams();

    // Check to make sure that all of the filters have some sort of value and store them in params to pass to the backend
    if (deviceType && deviceType !== 'null'){
      params = params.set('type', deviceType);
    }
    if (location && location !== 'null'){
      params = params.set('location', location);
    }

    if (isOn !== null && isOn !== undefined){
      params = params.set('isOn', String(isOn));
    }

    console.log('FINAL PARAMS:', params.toString());
    /**
         * Fetch devices from API, convert each DTO into SmartDevice,
         * then update the reactive state.
         */
    this.http.get<DeviceDto[]>(this.apiUrl, { params }).pipe(
      map( dtos => dtos.map( dto => this.mapDtoToModel(dto)))
      ).subscribe(processedDevices => {
        this.devices.set(processedDevices);
          });
        }

 /**
   * Maps backend DTO into frontend SmartDevice model.
   *
   */
private mapDtoToModel(dto : DeviceDto){
  return{
      uuid: dto.uuid,
      name: dto.name,
      location: dto.location,
      deviceType: dto.deviceType,

      // Turn state into a structured object
      state: {
        name: dto.state,
        availableTransitions: dto.availableTransitions || [],
        updatableFields: dto.updatableFields || []
        },

      isOn: dto.isOn,
      // Copy dynamic properties into attributes object
      attributes: { ...dto.properties }
    } as SmartDevice;
  }

/**
   * Executes a state transition/action on a device.
   */
executeAction(uuid: string, action: string, parameters: any){
  console.log('executing action: ', action);
  console.log('action parameters: ', parameters)
  return this.http.put(`${this.apiUrl}/${uuid}/state`, parameters , {params: {action: action} });
  }

 /**
   * Deletes a device by UUID.
   */
deleteDevice(uuid: string){

  return this.http.delete(`${this.apiUrl}/${uuid}`);
  }

  /**
   * Fetches historical logs for a specific device
   * and updates the reactive logs state.
   */
fetchLogs(uuid: string){

  this.http.get<DeviceLog[]>(`${this.apiUrl}/${uuid}/history`).subscribe({
    next: (logs) => this.currentLogs.set(logs),
    error: (err) => console.error(`Action failed`, err)
    });
  }

/**
   * Returns unique locations that contain thermostats only.
   * Useful for filtering temperature controls.
   */
  getThermostatLocations(){

    // Retrieve locations from all thermostats
    const locations = this.devices().filter(d => d.deviceType === 'THERMOSTAT').map(d => d.location);

    // Filter out duplicates and format for UI dropdown
    // Duplicates should not be possible given business logic, but it doesn't hurt to make sure
    return Array.from(new Set(locations)).map(location => ({
      label: location,
      value: location
      }));
    }

/**
   * Creates a new device via backend API.
   */
  createNewDevice(newDevice: any){
    return this.http.post(`${this.apiUrl}/create-device`, newDevice)
  }

  /**
   * Updates a single attribute of a device dynamically.
   * Uses computed property name to send only the changed field.
   */
  updateAttribute(uuid: string, attribute: string, newValue: any){

    return this.http.patch(`${this.apiUrl}/${uuid}/attribute`,
      { [attribute]: newValue
        });
    }
}
