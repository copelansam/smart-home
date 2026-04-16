import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DeviceService } from './device';
import { SmartDevice, SmartFan, SmartThermostat, SmartLight, SmartLock } from './device.model';
import { CommonModule, JsonPipe } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, JsonPipe],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit{
  protected readonly title = signal('frontend');
  protected deviceService = inject(DeviceService);

  ngOnInit(){
    this.deviceService.fetchDevices();
    }

  isLight(device: SmartDevice): device is SmartLight{
    return device.deviceType == 'LIGHT';
    }

  isFan(device: SmartDevice): device is SmartFan{
      return device.deviceType == 'FAN';
      }

  isDoorLock(device: SmartDevice): device is SmartLock{
      return device.deviceType == 'DOORLOCK';
      }

  isThermostat(device: SmartDevice): device is SmartThermostat{
      return device.deviceType == 'THERMOSTAT';
      }

  handleAction(uuid: string, action: string){
    this.deviceService.executeAction(uuid, action).subscribe({
      next: () => {
        console.log(`Action: ${action} sent!`);
        this.deviceService.fetchDevices();
        },
      error: (err) => console.error('Action failed', err)
      })
    }

  removeDevice(uuid: string){
    this.deviceService.deleteDevice(uuid).subscribe({
      next: () => {
        console.log(`Device Deleted!`);
        this.deviceService.fetchDevices();
        },
      error: (err) => console.error(`Action failed`, err)
    })
}
}
