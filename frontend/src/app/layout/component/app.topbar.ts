import { Component, inject } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StyleClassModule } from 'primeng/styleclass';
import { AppConfigurator } from './app.configurator';
import { LayoutService } from '@/app/layout/service/layout.service';
import { DeviceService } from '../../device';

@Component({
    selector: 'app-topbar',
    standalone: true,
    imports: [RouterModule, CommonModule, StyleClassModule, AppConfigurator],
    template: ` <div class="layout-topbar">
        <div class="layout-topbar-logo-container">

            <span>
              Smart Home Simulation
            </span>
        </div>

        <div class="layout-topbar-actions">
            <div class="layout-config-menu">
                <button> Add a Device </button>
                <button (click)="deviceService.factoryResetAllDevices()"> Factory Reset Devices </button>
                <button> Update A Location's Temperature </button>
                <button> Change Simulation Speed </button>
            </div>
        </div>
    </div>`
})
export class AppTopbar {
    items!: MenuItem[];

    layoutService = inject(LayoutService);
    deviceService = inject(DeviceService);
}
