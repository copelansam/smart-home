import { Component, inject } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StyleClassModule } from 'primeng/styleclass';
import { LayoutService } from '../../services/layout.service';
import { DeviceService } from '../../services/device.service';
import { SimulationService } from '../../services/simulation.service';
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
              <b>Smart Home Simulation</b>
            </span>
        </div>

        <div class="layout-topbar-actions">
            <div class="layout-config-menu">

            <!-- Create Device button and popover menu -->

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
                      <small>The thermostat will be created with the following default values:</small><br/>
                      <small>
                        <ul>
                          <li> State: Off</li>
                          <li> Mode:  Auto</li>
                          <li> Desired Temperature: 75 °F</li>
                          <li> Ambient Temperature: 60 °F</li>
                        </ul>
                      </small>
                      }
                    @if(this.newDeviceType==="LIGHT"){
                      <small>The light will be created with the following default values:</small><br/>
                      <small>
                        <ul>
                          <li> State: Off</li>
                          <li> Color: White</li>
                          <li> Brightness Percentage: 100 %</li>
                        </ul>
                      </small>
                      }
                    @if(this.newDeviceType==="FAN"){
                      <small>The fan will be created with the following default values:</small>
                      <small>
                        <ul>
                          <li> State: Off</li>
                          <li> Speed: Medium</li>
                        </ul>
                      </small>
                      }
                    @if(this.newDeviceType==="DOORLOCK"){
                      <small>The lock will be created with the following default values:</small>
                      <small>
                        <ul>
                          <li> State: Unlocked</li>
                        </ul>
                      </small>
                      }
                  </div>
                  <hr>

                  <button pButton (click)="createDevice()" (click)="createDevicePopover.toggle($event)">Create Device</button>
                </p-popover>

                <button (click)="op.toggle($event)" class="p-button-sm p-button-info">Simulation Controls</button>

                <p-popover #op>

                 <div class="control-section">
                            <label class="block font-bold mb-2">Update Speed</label>
                            <div class="flex gap-2">
                              <p-select [options]="speeds" [(ngModel)]="selectedSpeed" optionLabel="label" optionValue="value" placeholder="Select Speed" class="flex-1"></p-select>
                              <button pButton (click)="updateSimulationSpeed()">Update Simulation Speed</button>
                            </div>
                          </div>

                          <hr />

                          <label class="block font-bold mb-2">Update Ambient Temperature By Location</label>

                           <div class="flex gap-2">
                                      <p-select [options]="deviceService.getThermostatLocations()" [(ngModel)]="selectedLocation" optionLabel="label" optionValue="value" placeholder=Select a Location class="flex-1"></p-select>
                                      <input pInputText type="number" [(ngModel)]="tempValue" class="w-full" placeholder="°F" />
                                      <button pButton [disabled]="!selectedLocation" (click)="updateTemperature()"> Update Temperature </button>

                                  </div>
                           <hr />
                  <button class="p-button-sm p-button-danger ml-2" (click)="factoryResetDevices()">Factory Reset Devices</button>
                </p-popover>

            </div>
        </div>
    </div>`
})
export class AppTopbar {

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

    layoutService = inject(LayoutService);
    deviceService = inject(DeviceService);
    simulationService = inject(SimulationService);

    // List of device types
    deviceTypes = this.deviceService.getDeviceTypes();

    factoryResetDevices() {

      const confirmed = window.confirm(
            'Are you sure you want to factory reset all devices?\n\n' +
            'The following settings will be applied:\n\n' +
            '• Thermostat:\n' +
            '   - State: OFF\n' +
            '   - Desired Temperature: 75°F\n' +
            '   - Mode: AUTO\n\n' +
            '• Light:\n' +
            '   - State: OFF\n' +
            '   - Brightness: 100%\n' +
            '   - Color: WHITE\n\n' +
            '• Fan:\n' +
            '   - State: OFF\n' +
            '   - Speed: MEDIUM\n\n' +
            '• Door Lock:\n' +
            '   - State: UNLOCKED'
);
        this.simulationService.factoryResetAllDevices().subscribe({
          next: (res : any) => {
          },
          error: (err) => console.error('Device Reset Failed', err)
         });
  }

    updateTemperature(){
      if (!this.selectedLocation || this.tempValue === null) return;

      this.simulationService.updateLocationTemperature(this.selectedLocation, this.tempValue).subscribe({
        next: (res: any) =>{
          console.log(`Updating the temperature in: ${this.selectedLocation} to: ${this.tempValue} degrees F`);
          },
        error: (err: any) => {
                    console.error('Temperature update failed', err);
                }
        });
      }

    updateSimulationSpeed(){
      console.log("New Simulation Speed: ", this.selectedSpeed ,"X");
      this.simulationService.updateSimulationSpeed(this.selectedSpeed).subscribe({
        next: (res) => console.log('Speed updated successfully'),
        error: (err) => console.error('Failed', err)
        });
      }

    createDevice(){

      // assign fields common to all devices
      const newDevice: any = {
              deviceType: this.newDeviceType,
              name: this.newDeviceName,
              location: this.newDeviceLocation,
              }

      this.deviceService.createNewDevice(newDevice).subscribe({
        next: () => {
          },
        error: (err) => console.log('Device Creation Failed', err)
      });
    }
}
