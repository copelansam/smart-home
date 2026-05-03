import { Component, OnInit, inject, signal } from '@angular/core';
import { DeviceService } from './device';
import { SmartDevice, SmartFan, SmartThermostat, SmartLight, SmartLock, FAN_SPEEDS, THERMOSTAT_MODES } from './device.model';
import { CommonModule, JsonPipe, DatePipe } from '@angular/common';
import { SelectModule } from 'primeng/select';
import { FormsModule } from '@angular/forms';
import { PopoverModule } from 'primeng/popover';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, JsonPipe, DatePipe, SelectModule, FormsModule, PopoverModule ],
  templateUrl: './dashboard.html',
  styleUrl: './app.css'
})
export class DashboardComponent implements OnInit {
  protected readonly title = signal('Smart Home Dashboard');
  protected deviceService = inject(DeviceService);
  fanSpeeds = FAN_SPEEDS;
  thermostatModes = THERMOSTAT_MODES;

  // Variables used while editing device attributes
  selectedFanSpeed: string | null = null;
  selectedThermostatMode: string | null = null;
  selectedDesiredTemp: number | null = null;
  selectedBrightness: number | null = null;
  selectedRedValue: number | null = null;
  selectedGreenValue: number | null = null;
  selectedBlueValue: number | null = null;

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

handleAction(uuid: string, action: string) {

  let parameters: Record<string, any> = {};

  switch(action) {

    case 'UPDATE_SPEED':
      parameters = { 'speed' : this.selectedFanSpeed };
      break;

    case 'UPDATE_BRIGHTNESS':
      parameters = { 'brightnessPercentage' : this.selectedBrightness};
      break;

    case 'UPDATE_COLOR':
      parameters = { 'redValue': this.selectedRedValue,
                     'greenValue': this.selectedGreenValue,
                     'blueValue': this.selectedBlueValue};
      break;

    case 'UPDATE_DESIRED_TEMP':
      parameters = { 'desiredTemp' : this.selectedDesiredTemp};
      break;

    case 'UPDATE_MODE':
      parameters = { 'mode': this.selectedThermostatMode };
      break;

    default:
      parameters = null;
    }


  this.deviceService.executeAction(uuid, action, parameters).subscribe({
    next: () => {
      console.log(`${action} successful`);
      this.deviceService.fetchDevices('null','null',null);
    },
    error: (err) => alert('Action failed: ' + err.message + 'Parameters: ' + parameters)
  });
  }

  prepareOnFanEdit(currentSpeed: string){
    this.selectedFanSpeed = currentSpeed;
    }

  prepareOnThermostatEdit(mode: string, desiredTemp: number){
    this.selectedThermostatMode = mode;
    this.selectedDesiredTemp = desiredTemp;
  }

  prepareOnLightEdit(brightnessPercentage: number, redValue: number, greenValue: number, blueValue: number){
    this.selectedBrightness = brightnessPercentage;
    this.selectedRedValue = redValue;
    this.selectedGreenValue = greenValue;
    this.selectedBlueValue = blueValue;
    }
}
