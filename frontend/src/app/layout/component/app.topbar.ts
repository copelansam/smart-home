import { Component, inject } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StyleClassModule } from 'primeng/styleclass';
import { LayoutService } from '@/app/layout/service/layout.service';
import { DeviceService } from '../../device';
import { MenuModule } from 'primeng/menu';
import { PopoverModule } from 'primeng/popover';
import { FormsModule } from '@angular/forms';
import { SelectModule } from 'primeng/select';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';

@Component({
    selector: 'app-topbar',
    standalone: true,
    imports: [RouterModule, CommonModule, StyleClassModule, MenuModule, PopoverModule, FormsModule, SelectModule, ButtonModule, InputTextModule ],
    template: ` <div class="layout-topbar">
        <div class="layout-topbar-logo-container">

            <span>
              Smart Home Simulation
            </span>
        </div>
<button (click)="op.toggle($event)" class="p-button-sm p-button-info">Simulation Controls</button>


<p-popover #op>

 <div class="control-section">
            <label class="block font-bold mb-2">Update Speed</label>
            <div class="flex gap-2">
              <p-select [options]="speeds" [(ngModel)]="selectedSpeed" optionLabel="label" optionValue="value" placeholder="Select Speed" class="flex-1"></p-select>
              <button pButton icon="pi pi-check" (click)="updateSimulationSpeed()"></button>
            </div>
          </div>

          <hr />

          <label class="block font-bold mb-2">Update Ambient Temperature By Location</label>

           <div class="flex gap-2">
                      <p-select [options]="getThermostatLocations()" [(ngModel)]="selectedLocation" optionLabel="label" optionValue="value" placeholder=Select a Location class="flex-1"></p-select>
                      <input pInputText type="number" [(ngModel)]="tempValue" class="w-full" placeholder="°F" />
                      <button pButton [disabled]="!selectedLocation" (click)="updateTemperature()"> Update Temperature </button>

                  </div>
           <hr />
  <button class="p-button-sm p-button-danger ml-2" (click)="factoryResetDevices()">Factory Reset Devices</button>
</p-popover>

        <div class="layout-topbar-actions">
            <div class="layout-config-menu">
                <button class="p-button-sm p-button-info" (click)="createDevicePopover.toggle($event)"> Add a Device </button>

                <p-popover #createDevicePopover [style]="{ width: '33vw' }">
                  <div class="shared-info">
                    <h3>Create a Device</h3>
                    <hr/>
                    Device Type: <p-select [options]=deviceTypes [(ngModel)]="newDeviceType" optionLabel="label" optionValue="value" placeholder="Select a Device Type" class="flex-1"></p-select> <br>
                    <span>Name: <input pInputText [(ngModel)]="newDeviceName" type="string" placeholder="Device Name"/></span> <br>
                    <span>Location: <input pInputText [(ngModel)]="newDeviceLocation" type="string" placeholder="Location"></span>
                  </div>
                  <div class="unique-info">
                    @if(this.newDeviceType==="THERMOSTAT"){
                      <label class="w-4">Desired Temperature: </label><input pInputText [(ngModel)]="desiredTemp" class="w-8" type="number" min="60" max="80" placeholder="Desired Temperature (60 - 80)"><br>
                      <label class="w-4">Ambient Temperature: </label><input pInputText [(ngModel)]="ambientTemp" class="w-8" type="number" placeholder="Ambient Temperature">
                      }
                    @if(this.newDeviceType==="LIGHT"){
                      <label class="w-4">Brightness Percentage: </label><input pInputText [(ngModel)]="brightnessPercentage" class="w-8" type="number" min="10" max="100" placeholder="Brightness Percentage (10 - 100)"> <br>
                      <label class="w-3">Color: </label> <br>
                      <input pInputText [(ngModel)]="redValue" class="w-4" type="number" min="0" max="255" placeholder="R Value (0 - 255)">
                      <input pInputText [(ngModel)]="greenValue" class="w-4" type="number" min="0" max="255" placeholder="G Value (0 - 255)">
                      <input pInputText [(ngModel)]="blueValue" class="w-4" type="number" min="0" max="255" placeholder="B Value (0 - 255)">
                      }
                  </div>
                  <hr>

                  <button pButton (click)="createDevice()" (click)="createDevicePopover.toggle($event)">Create Device</button>
                </p-popover>
            </div>
        </div>
    </div>`
})
export class AppTopbar {

// List of device types
  deviceTypes = [
    {label: 'Thermostat' , value: 'THERMOSTAT'},
    {label: 'Light' , value: 'LIGHT'},
    {label: 'Door Lock' , value: 'DOORLOCK'},
    {label: 'Fan' , value: 'FAN'}
    ];

// Simulation Variables
  selectedSpeed: number = 1;

  selectedLocation: string | null = null;

  tempValue: number;
    items: MenuItem[]
    speeds = [
      {label: '1x' , value: 1},
      {label: '5x', value: 5},
      {label: '10x', value: 10}
      ];

// Variables used for device creation

  // Variables shared across all device types
  newDeviceName: string = '';
  newDeviceLocation: string = '';
  newDeviceType: string = 'THERMOSTAT';

  // Thermostat exclusive variables
  desiredTemp?: number;
  ambientTemp?: number;

  // Light exclusive variables
  brightnessPercentage?: number;
  redValue?: number;
  greenValue?: number;
  blueValue?: number;


    layoutService = inject(LayoutService);
    deviceService = inject(DeviceService);

    factoryResetDevices() {
        this.deviceService.factoryResetAllDevices().subscribe({
          next: (res : any) => {
            console.log(res.message);
            this.deviceService.fetchDevices(); // refresh UI across app
          },
          error: (err) => console.error('Device Reset Failed', err)
         });
  }

    getThermostatLocations(){

      const devices = this.deviceService.devices() || [];
      return devices.filter(device => device.deviceType === 'THERMOSTAT').map(device => ({label: device.location, value: device.location}));

      }

    updateTemperature(){
      if (!this.selectedLocation || this.tempValue === null) return;

      this.deviceService.updateLocationTemperature(this.selectedLocation, this.tempValue).subscribe({
        next: (res: any) =>{
          console.log(`Updating the temperature in: ${this.selectedLocation} to: ${this.tempValue} degrees F`),
          this.deviceService.fetchDevices();
          },
        error: (err: any) => {
                    console.error('Temperature update failed', err);
                }
        });
      }

    updateSimulationSpeed(){
      console.log("New Simulation Speed: ", this.selectedSpeed ,"X");
      this.deviceService.updateSimulationSpeed(this.selectedSpeed);
      }

    createDevice(){

      // Assign device specific attributes
      const attributes : any = {};

      if (this.newDeviceType === 'THERMOSTAT'){
        attributes.ambientTemperature = this.ambientTemp;
        attributes.desiredTemperature = this.desiredTemp;
        }

      if (this.newDeviceType === 'LIGHT'){
        attributes.brightnessPercentage = this.brightnessPercentage;
        attributes.redValue = this.redValue;
        attributes.greenValue = this.greenValue;
        attributes.blueValue = this.blueValue;
        }

      // assign fields common to all devices
      const newDevice: any = {
              deviceType: this.newDeviceType,
              name: this.newDeviceName,
              location: this.newDeviceLocation,
              attributes: attributes
              }

      console.log("Creating Device!", newDevice);
      this.deviceService.createNewDevice(newDevice).subscribe({
        next: () => {
          this.deviceService.fetchDevices();
          },
        error: (err) => console.log('Device Creation Failed', err)
      });
    }
}
