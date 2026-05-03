import { Component, OnInit, inject, signal } from '@angular/core';
import { DeviceService } from './device';
import { SmartDevice, SmartFan, SmartThermostat, SmartLight, SmartLock, FAN_SPEEDS, THERMOSTAT_MODES } from './device.model';
import { CommonModule, JsonPipe, DatePipe } from '@angular/common';
import { SelectModule } from 'primeng/select';
import { FormsModule } from '@angular/forms';
import { PopoverModule } from 'primeng/popover';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, JsonPipe, DatePipe, SelectModule, FormsModule, PopoverModule, ToastModule ],
  providers: [ MessageService ],
  templateUrl: './dashboard.html',
  styleUrl: './app.css'
})
export class DashboardComponent implements OnInit {
  private messageService = inject(MessageService);
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


  // Upon initial loading of the page, load all devices
  ngOnInit() {
    this.deviceService.fetchDevices('null', 'null', null);

     setTimeout(() => {
        console.log(this.deviceService.devices());
      }, 1000);
  }

  // Check what type of device the device is. Used to display device specific information
  isLight(device: SmartDevice): device is SmartLight { return device.deviceType == 'LIGHT'; }
  isFan(device: SmartDevice): device is SmartFan { return device.deviceType == 'FAN'; }
  isDoorLock(device: SmartDevice): device is SmartLock { return device.deviceType == 'DOORLOCK'; }
  isThermostat(device: SmartDevice): device is SmartThermostat { return device.deviceType == 'THERMOSTAT'; }

  // Deletes the specified device, device's logs will still be visible through API calls
  removeDevice(device: SmartDevice) {
    this.deviceService.deleteDevice(device.uuid).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: `${ device.name } was successfully deleted`
          });
        this.deviceService.fetchDevices('null', 'null', null);
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: `Failed to delete ${ device.name}`
          });
        }
    });
  }

  // Loads the specified device's logs
  viewLogs(device: SmartDevice) {
    this.selectedDevice.set(device);
    this.deviceService.fetchLogs(device.uuid);
  }
  // Reset the selected device to null when closing device logs
  closeLogs() {
    this.selectedDevice.set(null);
  }

// Turns a light's RGB value into a CSS rgb color function to display the correct color
getRGBString(color : any) : string{
  if (!color) return `black`;
  return `rgb(${color[0]}, ${color[1]}, ${color[2]})`;
  }

// Handles actions including turn on / off and updating fields
handleAction(uuid: string, action: string) {

  let parameters: Record<string, any> =this.getActionParameters(action);

  this.deviceService.executeAction(uuid, action, parameters).subscribe({
      next: () => {
        // Generate success message
        const detailMessage = this.getSuccessMessage(action, parameters);

        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: detailMessage,
          life: 2000
        });

        // fetch updated devices
        this.deviceService.fetchDevices('null', 'null', null);
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: `${action} failed to execute. Please try again.`,
          life: 2000
        });
        console.error(err);
      }
    });
  }

  // Assigns updatable fields to the values in of the device used for placeholder values
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

  // Generates parameters to pass to the backend based on what action is being performed
  private getActionParameters(action: string): Record<string, any> {
    switch(action) {
      case 'UPDATE_SPEED':
        return { 'speed' : this.selectedFanSpeed };
      case 'UPDATE_BRIGHTNESS':
        return { 'brightnessPercentage' : this.selectedBrightness };
      case 'UPDATE_COLOR':
        return { 'redValue': this.selectedRedValue, 'greenValue': this.selectedGreenValue, 'blueValue': this.selectedBlueValue };
      case 'UPDATE_DESIRED_TEMP':
        return { 'desiredTemp' : this.selectedDesiredTemp };
      case 'UPDATE_MODE':
        return { 'mode': this.selectedThermostatMode };
      default:
        return {};
    }
  }

  // Generates success message based on what actin is being performed
  private getSuccessMessage(action: string, parameters: Record<string, any>): string {
    const entries = Object.entries(parameters);

    if (entries.length === 0) {
      return `Action ${action} has been executed successfully`;
    }

    const mapping = entries.map(([key, value]) => `${key} updated to ${value}`).join(', ');
    return mapping;
  }
}
