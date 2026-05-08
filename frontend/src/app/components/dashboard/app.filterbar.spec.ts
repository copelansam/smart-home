import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal, computed } from '@angular/core';
import { AppFilterBar } from './app.filterbar';
import { DeviceService } from '../../services/device.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SmartDevice } from '../../models/device.model';
import { vi } from 'vitest';

// ---------------------------------------------------------------------------
// Mock DeviceService
// ---------------------------------------------------------------------------

function makeMockDeviceService(devices: SmartDevice[] = []) {
  const devicesSignal = signal<SmartDevice[]>(devices);
  return {
    devices: devicesSignal,
    locations: computed(() =>
      Array.from(new Set(devicesSignal().map(d => d.location))).map(loc => ({
        label: loc,
        value: loc,
      }))
    ),
    fetchDevices: vi.fn(),
    getDeviceTypes: () => [
      { label: 'Thermostat', value: 'THERMOSTAT' },
      { label: 'Light', value: 'LIGHT' },
      { label: 'Door Lock', value: 'DOORLOCK' },
      { label: 'Fan', value: 'FAN' },
    ],
  };
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('AppFilterBar — filter behavior', () => {
  let fixture: ComponentFixture<AppFilterBar>;
  let component: AppFilterBar;
  let mockDeviceService: ReturnType<typeof makeMockDeviceService>;

  async function setup(devices: SmartDevice[] = []) {
    mockDeviceService = makeMockDeviceService(devices);

    await TestBed.configureTestingModule({
      imports: [AppFilterBar, CommonModule, FormsModule],
      providers: [{ provide: DeviceService, useValue: mockDeviceService }],
    }).compileComponents();

    fixture = TestBed.createComponent(AppFilterBar);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  }

  // ── Initial state ─────────────────────────────────────────────────────────

  it('initializes with all filters set to null', async () => {
    await setup();
    expect(component.deviceTypeFilter).toBeNull();
    expect(component.deviceLocationFilter).toBeNull();
    expect(component.devicePowerStatusFilter).toBeNull();
  });

  it('includes an All option in device type filters', async () => {
    await setup();
    const allOption = component.deviceTypes.find(t => t.value === null);
    expect(allOption).toBeTruthy();
    expect(allOption?.label).toBe('All');
  });

  it('includes All, On, and Off power status options', async () => {
    await setup();
    const values = component.powerStatuses.map(p => p.value);
    expect(values).toContain(null);
    expect(values).toContain(true);
    expect(values).toContain(false);
  });

  // ── filterSmartDevices ────────────────────────────────────────────────────

  it('calls fetchDevices with null filters when nothing is selected', async () => {
    await setup();
    component.filterSmartDevices();
    expect(mockDeviceService.fetchDevices).toHaveBeenCalledWith(null, null, null);
  });

  it('calls fetchDevices with selected device type', async () => {
    await setup();
    component.deviceTypeFilter = 'LIGHT';
    component.filterSmartDevices();
    expect(mockDeviceService.fetchDevices).toHaveBeenCalledWith('LIGHT', null, null);
  });

  it('calls fetchDevices with selected location', async () => {
    await setup();
    component.deviceLocationFilter = 'Office';
    component.filterSmartDevices();
    expect(mockDeviceService.fetchDevices).toHaveBeenCalledWith(null, 'Office', null);
  });

  it('calls fetchDevices with isOn=true when On is selected', async () => {
    await setup();
    component.devicePowerStatusFilter = true;
    component.filterSmartDevices();
    expect(mockDeviceService.fetchDevices).toHaveBeenCalledWith(null, null, true);
  });

  it('calls fetchDevices with isOn=false when Off is selected', async () => {
    await setup();
    component.devicePowerStatusFilter = false;
    component.filterSmartDevices();
    expect(mockDeviceService.fetchDevices).toHaveBeenCalledWith(null, null, false);
  });

  it('calls fetchDevices with all three filters combined', async () => {
    await setup();
    component.deviceTypeFilter = 'FAN';
    component.deviceLocationFilter = 'Bedroom';
    component.devicePowerStatusFilter = true;
    component.filterSmartDevices();
    expect(mockDeviceService.fetchDevices).toHaveBeenCalledWith('FAN', 'Bedroom', true);
  });

  // ── resetFilters ──────────────────────────────────────────────────────────

  it('resets all filters to null/null/null and calls fetchDevices', async () => {
    await setup();
    component.deviceTypeFilter = 'LIGHT';
    component.deviceLocationFilter = 'Office';
    component.devicePowerStatusFilter = true;

    component.resetFilters();

    expect(mockDeviceService.fetchDevices).toHaveBeenCalledWith('null', 'null', null);
  });

  it('sets deviceTypeFilter to null string after reset', async () => {
    await setup();
    component.deviceTypeFilter = 'THERMOSTAT';
    component.resetFilters();
    expect(component.deviceTypeFilter).toBe('null');
  });

  it('sets deviceLocationFilter to null string after reset', async () => {
    await setup();
    component.deviceLocationFilter = 'Kitchen';
    component.resetFilters();
    expect(component.deviceLocationFilter).toBe('null');
  });

  it('sets devicePowerStatusFilter to null after reset', async () => {
    await setup();
    component.devicePowerStatusFilter = false;
    component.resetFilters();
    expect(component.devicePowerStatusFilter).toBeNull();
  });
});

