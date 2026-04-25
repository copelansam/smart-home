import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { SmartDevice, DeviceType, DeviceLog } from './device.model'

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

  devices = signal<SmartDevice[]>([]);
  currentLogs = signal<DeviceLog[]>([]);

  fetchDevices(){
    this.http.get<DeviceDto[]>(this.apiUrl).pipe(
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

  updateLocationTemperature(location: string, temperature: number){

    return this.http.post(`${this.simulationUrl}/location/temperature`, {location, temperature}, { responseType: 'text' });
  }
}
