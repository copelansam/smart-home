import { Component, OnInit, inject, signal } from '@angular/core';
import { DeviceService } from './device';
import { SmartDevice, SmartFan, SmartThermostat, SmartLight, SmartLock } from './device.model';
import { CommonModule, JsonPipe, DatePipe } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, JsonPipe, DatePipe],
  templateUrl: './dashboard.html',
  styleUrl: './app.css'
})
export class DashboardComponent implements OnInit {
  protected readonly title = signal('Smart Home Dashboard');
  protected deviceService = inject(DeviceService);
  selectedDevice = signal<SmartDevice | null>(null);

  ngOnInit() {
    this.deviceService.fetchDevices('null', 'null', null);

     setTimeout(() => {
        console.log(this.deviceService.devices());
      }, 1000);
  }

  isLight(device: SmartDevice): device is SmartLight { return device.deviceType == 'LIGHT'; }
  isFan(device: SmartDevice): device is SmartFan { return device.deviceType == 'FAN'; }
  isDoorLock(device: SmartDevice): device is SmartLock { return device.deviceType == 'DOORLOCK'; }
  isThermostat(device: SmartDevice): device is SmartThermostat { return device.deviceType == 'THERMOSTAT'; }

  handleAction(uuid: string, action: string) {
    this.deviceService.executeAction(uuid, action).subscribe({
      next: () => {
        console.log(`Action: ${action} sent!`);
        this.deviceService.fetchDevices('null', 'null', null);
      },
      error: (err) => console.error('Action failed', err)
    });
  }

  removeDevice(uuid: string) {
    this.deviceService.deleteDevice(uuid).subscribe({
      next: () => {
        console.log(`Device Deleted!`);
        this.deviceService.fetchDevices('null', 'null', null);
      },
      error: (err) => console.error(`Action failed`, err)
    });
  }

  viewLogs(device: SmartDevice) {
    this.selectedDevice.set(device);
    this.deviceService.fetchLogs(device.uuid);
  }

  closeLogs() {
    this.selectedDevice.set(null);
  }

getRGBString(color : any) : string{
  if (!color) return `black`;
  return `rgb(${color[0]}, ${color[1]}, ${color[2]})`;
  }
}
