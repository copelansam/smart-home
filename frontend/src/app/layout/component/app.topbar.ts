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
<button (click)="op.toggle($event)">Simulation Controls</button>

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
                <button> Add a Device </button>
            </div>
        </div>
    </div>`
})
export class AppTopbar {

  selectedSpeed: number = 1;
  selectedLocation: string | null = null;
  tempValue: number;
    items: MenuItem[]
    speeds = [
      {label: '1x' , value: 1},
      {label: '5x', value: 5},
      {label: '10x', value: 10}
      ];


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
}
