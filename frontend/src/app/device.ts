import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { SmartDevice, DeviceType, DeviceLog } from './device.model'
import { computed } from '@angular/core';

interface DeviceDto{
  uuid: string;
  name: string;
  location: string;
  deviceType: DeviceType;
  state: any;
  isOn: boolean;
  properties: Record<string, any>;
  }

@Injectable({
  providedIn: 'root',
})
export class DeviceService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/devices';
  private simulationUrl = 'http://localhost:8080/api/simulation';

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

    return Array.from(new Set(allLocations)).map(loc => ({
      label: loc,
      value: loc
      }));
    });

  fetchDevices(deviceType ?: string, location ?: string, isOn ?: boolean){

    let params = new HttpParams();

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
      state: dto.state,
      isOn: dto.isOn,
      attributes: { ...dto.properties }
    } as SmartDevice;
  }

executeAction(uuid: string, action: string){
  return this.http.post(`${this.apiUrl}/${uuid}/state`, {action});
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

  updateSimulationSpeed(speed: number){
    return this.http.post(`${this.simulationUrl}/speed`, { speed });
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
}
