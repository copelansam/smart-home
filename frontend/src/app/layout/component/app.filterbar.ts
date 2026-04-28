import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StyleClassModule } from 'primeng/styleclass';
import { LayoutService } from '@/app/layout/service/layout.service';
import { DeviceService } from '../../device';
import { FormsModule } from '@angular/forms';
import { SelectButtonModule } from 'primeng/selectbutton';
import { SelectModule } from 'primeng/select';

@Component({
  selector: 'app-filterbar',
  standalone: true,
  imports: [ RouterModule, CommonModule, StyleClassModule, FormsModule, SelectButtonModule, SelectModule ],
  template: `<div class="layout-filterbar" style="position:sticky; top: 55px; padding:5px; background-color:white;">
              <span>Filter Devices: </span>
              Device Type: <p-selectButton [options]="deviceTypes" [(ngModel)]="deviceTypeFilter" optionLabel="label" optionValue="value"></p-selectButton>
              Location: <p-select [options]="deviceService.locations()" [(ngModel)]="deviceLocationFilter" optionLabel="label" optionValue="value"></p-select>
              Power Status: <p-selectButton [options]="powerStatuses" [(ngModel)]="devicePowerStatusFilter" optionLabel="label" optionValue="value"></p-selectButton>
              <button class="p-button-sm p-button-info" (click)="filterSmartDevices()">Apply Filters</button>
              <button class="p-button-sm p-button-info" (click)="resetFilters()">Reset Filters</button>
             </div>
              `


  })

export class AppFilterBar {

  deviceTypeFilter: string | null = null;
  deviceLocationFilter: string | null = null;
  devicePowerStatusFilter: boolean | null = null;

  layoutService = inject(LayoutService);
  deviceService = inject(DeviceService);


  // List of device types
  deviceTypes = [
      { label: 'All', value: null },
      ...this.deviceService.getDeviceTypes()
      ];

  powerStatuses = [
    { label: 'All', value: null},
    { label: 'On', value: true},
    { label: 'Off', value: false}
    ]

  filterSmartDevices(){

    this.deviceService.fetchDevices(this.deviceTypeFilter, this.deviceLocationFilter, this.devicePowerStatusFilter);
    }

  resetFilters(){
    this.deviceTypeFilter = 'null';
    this.deviceLocationFilter = 'null';
    this.devicePowerStatusFilter = null;
    this.deviceService.fetchDevices('null','null',null);
    }
  }

