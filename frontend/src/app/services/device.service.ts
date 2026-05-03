import { Injectable, inject, signal, NgZone } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { RxStomp } from '@stomp/rx-stomp';
import { map, tap } from 'rxjs/operators';
import { SmartDevice, DeviceType, DeviceLog, ITransition } from '../models/device.model'
import { computed } from '@angular/core';

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

@Injectable({
  providedIn: 'root',
})
export class DeviceService {
  private http = inject(HttpClient);
   private zone = inject(NgZone);
  private apiUrl = 'http://localhost:8080/api/devices';
  private simulationUrl = 'http://localhost:8080/api/simulation';

  private rxStomp = new RxStomp();

  constructor() {
      this.initializeWebSocket();
    }

    private initializeWebSocket() {
      this.rxStomp.configure({
        brokerURL: 'ws://localhost:8080/ws/websocket',
        reconnectDelay: 5000, // Auto-reconnect after 5 seconds if connection drops
        debug: (str) => console.log(str),
      });

     this.rxStomp.activate();

          this.rxStomp.watch('/topic/devices').pipe(
                map(message => JSON.parse(message.body) as DeviceDto),
                map(dto => this.mapDtoToModel(dto))
              ).subscribe((updatedDevice: SmartDevice) => {

                this.zone.run(() => {
                  this.devices.update(currentDevices => {

                    const updatedArray = currentDevices.map(device =>
                      device.uuid === updatedDevice.uuid ? updatedDevice : device
                    );

                    return [...updatedArray];
                  });
                });

              });
       }

  getDeviceTypes() {
      return [
        { label: 'Thermostat', value: 'THERMOSTAT' },
        { label: 'Light', value: 'LIGHT' },
        { label: 'Door Lock', value: 'DOORLOCK' },
        { label: 'Fan', value: 'FAN' }
      ];
    }

  devices = signal<SmartDevice[]>([]);
  currentLogs = signal<DeviceLog[]>([]);
  allLocations = signal<{ label: string, value: string }[]>([]);

  locations = computed(() => {
    const allLocations = this.devices().map(d => d.location);

    // Creates a list of all unique locations and assigns them a label and value so that they can be selected by the user
    return Array.from(new Set(allLocations)).map(loc => ({
      label: loc,
      value: loc
      }));
    });

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
    this.http.get<DeviceDto[]>(this.apiUrl, { params }).pipe(
      map( dtos => dtos.map( dto => this.mapDtoToModel(dto)))
      ).subscribe(processedDevices => {
        this.devices.set(processedDevices);
          });
        }

private mapDtoToModel(dto : DeviceDto){
  return{
      uuid: dto.uuid,
      name: dto.name,
      location: dto.location,
      deviceType: dto.deviceType,
      state: {
        name: dto.state,
        availableTransitions: dto.availableTransitions || [],
        updatableFields: dto.updatableFields || []
        },
      isOn: dto.isOn,
      attributes: { ...dto.properties }
    } as SmartDevice;
  }

executeAction(uuid: string, action: string, parameters: any){
  console.log('executing action: ', action);
  console.log('action parameters: ', parameters)
  return this.http.put(`${this.apiUrl}/${uuid}/state`, parameters , {params: {action: action} });
  }

deleteDevice(uuid: string){
  return this.http.delete(`${this.apiUrl}/${uuid}`);
  }

fetchLogs(uuid: string){

  this.http.get<DeviceLog[]>(`${this.apiUrl}/${uuid}/history`).subscribe({
    next: (logs) => this.currentLogs.set(logs),
    error: (err) => console.error(`Action failed`, err)
    });
  }

  factoryResetAllDevices(){
    return this.http.post(`${this.simulationUrl}/reset`, null);
    }

  updateSimulationSpeed(timeMultiplier: number){
    console.log('Updating speed to: ' + timeMultiplier);
    return this.http.post(`${this.simulationUrl}/speed`, null , {params: { 'timeMultiplier': timeMultiplier}, responseType: 'text'} );
    }

  getThermostatLocations(){
    const locations = this.devices().filter(d => d.deviceType === 'THERMOSTAT').map(d => d.location);

    return Array.from(new Set(locations)).map(location => ({
      label: location,
      value: location
      }));
    }

  updateLocationTemperature(location: string, temperature: number){

    return this.http.post(`${this.simulationUrl}/location/temperature`, {location, temperature}, { responseType: 'text' });
  }

  createNewDevice(newDevice: any){
    return this.http.post(`${this.apiUrl}/create-device`, newDevice)
  }

  updateAttribute(uuid: string, attribute: string, newValue: any){

    return this.http.patch(`${this.apiUrl}/${uuid}/attribute`,
      { [attribute]: newValue
        });
    }
}
