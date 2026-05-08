import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { DashboardComponent } from './dashboard';
import { DeviceService } from '../../services/device.service';
import { MessageService } from 'primeng/api';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SmartDevice, SmartLight, SmartFan, SmartThermostat, SmartLock } from '../../models/device.model';
import { of } from 'rxjs';
import { vi } from 'vitest';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function makeLight(overrides: Partial<SmartLight> = {}): SmartLight {
  return {
    uuid: 'light-uuid-1',
    name: 'Test Light',
    location: 'Office',
    deviceType: 'LIGHT',
    isOn: false,
    state: {
      name: 'Light Off',
      materialIcon: 'power_off',
      availableTransitions: [{ action: 'TURN_LIGHT_ON', label: 'Turn On' }],
      updatableFields: [],
    },
    attributes: { brightnessPercentage: 100, color: [255, 255, 255] },
    ...overrides,
  };
}

function makeFan(overrides: Partial<SmartFan> = {}): SmartFan {
  return {
    uuid: 'fan-uuid-1',
    name: 'Test Fan',
    location: 'Office',
    deviceType: 'FAN',
    isOn: false,
    state: {
      name: 'Fan Off',
      materialIcon: 'power_off',
      availableTransitions: [{ action: 'TURN_FAN_ON', label: 'Turn On' }],
      updatableFields: [],
    },
    attributes: { speed: 'MEDIUM' },
    ...overrides,
  };
}

function makeThermostat(overrides: Partial<SmartThermostat> = {}): SmartThermostat {
  return {
    uuid: 'thermostat-uuid-1',
    name: 'Test Thermostat',
    location: 'Office',
    deviceType: 'THERMOSTAT',
    isOn: false,
    state: {
      name: 'Thermostat Off',
      materialIcon: 'power_off',
      availableTransitions: [{ action: 'POWER_THERMOSTAT_ON', label: 'Turn On' }],
      updatableFields: [
        { action: 'UPDATE_DESIRED_TEMP', label: 'Update Desired Temp' },
        { action: 'UPDATE_MODE', label: 'Update Mode' },
      ],
    },
    attributes: { ambientTemperature: 60, desiredTemperature: 75, mode: 'AUTO' },
    ...overrides,
  };
}

function makeLock(overrides: Partial<SmartLock> = {}): SmartLock {
  return {
    uuid: 'lock-uuid-1',
    name: 'Test Lock',
    location: 'Office',
    deviceType: 'DOORLOCK',
    isOn: true,
    state: {
      name: 'Door Unlocked',
      materialIcon: 'lock_open',
      availableTransitions: [{ action: 'LOCK', label: 'Lock' }],
      updatableFields: [],
    },
    attributes: {},
    ...overrides,
  };
}

// ---------------------------------------------------------------------------
// Mock DeviceService
// ---------------------------------------------------------------------------

function makeMockDeviceService(devices: SmartDevice[] = []) {
  const devicesSignal = signal<SmartDevice[]>(devices);
  const locationsSignal = signal(
    Array.from(new Set(devices.map(d => d.location))).map(loc => ({ label: loc, value: loc }))
  );

  return {
    devices: devicesSignal,
    locations: locationsSignal,
    currentLogs: signal([]),
    fetchDevices: vi.fn(),
    executeAction: vi.fn().mockReturnValue(of({})),
    deleteDevice: vi.fn().mockReturnValue(of({})),
    fetchLogs: vi.fn(),
  };
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('DashboardComponent — rendering', () => {
  let fixture: ComponentFixture<DashboardComponent>;
  let compiled: HTMLElement;
  let mockDeviceService: ReturnType<typeof makeMockDeviceService>;

  async function setup(devices: SmartDevice[]) {
    mockDeviceService = makeMockDeviceService(devices);

    await TestBed.configureTestingModule({
      imports: [DashboardComponent, CommonModule, FormsModule],
      providers: [
        { provide: DeviceService, useValue: mockDeviceService },
        MessageService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
    compiled = fixture.nativeElement as HTMLElement;
  }

  // ── No devices ────────────────────────────────────────────────────────────

  it('renders nothing when device list is empty', async () => {
    await setup([]);
    const cards = compiled.querySelectorAll('.card');
    expect(cards.length).toBe(0);
  });

  // ── Light ─────────────────────────────────────────────────────────────────

  it('renders a light card with brightness and color swatch', async () => {
    await setup([makeLight()]);
    expect(compiled.textContent).toContain('Test Light');
    expect(compiled.textContent).toContain('LIGHT');
    expect(compiled.textContent).toContain('Brightness Percentage');
    expect(compiled.querySelector('[style*="background"]')).toBeTruthy();
  });

  it('shows Turn On button for a light that is off', async () => {
    await setup([makeLight({ isOn: false })]);
    expect(compiled.textContent).toContain('Turn On');
  });

  it('shows Turn Off button for a light that is on', async () => {
    await setup([
      makeLight({
        isOn: true,
        state: {
          name: 'Light On',
          materialIcon: 'lightbulb',
          availableTransitions: [{ action: 'TURN_LIGHT_OFF', label: 'Turn Off' }],
          updatableFields: [
            { action: 'UPDATE_BRIGHTNESS', label: 'Update Brightness' },
            { action: 'UPDATE_COLOR', label: 'Update Color' },
          ],
        },
      }),
    ]);
    expect(compiled.textContent).toContain('Turn Off');
  });

  it('applies device-on CSS class when light is on', async () => {
    await setup([makeLight({ isOn: true, state: { name: 'Light On', materialIcon: 'lightbulb', availableTransitions: [], updatableFields: [] } })]);
    const card = compiled.querySelector('.card');
    expect(card?.classList.contains('device-on')).toBe(true);
  });

  it('applies device-off CSS class when light is off', async () => {
    await setup([makeLight({ isOn: false })]);
    const card = compiled.querySelector('.card');
    expect(card?.classList.contains('device-off')).toBe(true);
  });

  // ── Fan ───────────────────────────────────────────────────────────────────

  it('renders a fan card with speed attribute', async () => {
    await setup([makeFan()]);
    expect(compiled.textContent).toContain('Test Fan');
    expect(compiled.textContent).toContain('FAN');
    expect(compiled.textContent).toContain('Fan Speed');
    expect(compiled.textContent).toContain('MEDIUM');
  });

  it('shows Turn On button for a fan that is off', async () => {
    await setup([makeFan({ isOn: false })]);
    expect(compiled.textContent).toContain('Turn On');
  });

  // ── Thermostat ────────────────────────────────────────────────────────────

  it('renders a thermostat card with temperature and mode attributes', async () => {
    await setup([makeThermostat()]);
    expect(compiled.textContent).toContain('Test Thermostat');
    expect(compiled.textContent).toContain('THERMOSTAT');
    expect(compiled.textContent).toContain('Ambient Temperature');
    expect(compiled.textContent).toContain('Desired Temperature');
    expect(compiled.textContent).toContain('Mode');
  });

  it('shows Turn On button for a thermostat that is off', async () => {
    await setup([makeThermostat()]);
    expect(compiled.textContent).toContain('Turn On');
  });

  // ── Door Lock ─────────────────────────────────────────────────────────────

  it('renders a door lock card with current state', async () => {
    await setup([makeLock()]);
    expect(compiled.textContent).toContain('Test Lock');
    expect(compiled.textContent).toContain('DOORLOCK');
    expect(compiled.textContent).toContain('Door Unlocked');
  });

  it('shows Lock button for an unlocked door', async () => {
    await setup([makeLock()]);
    expect(compiled.textContent).toContain('Lock');
  });

  it('shows Unlock button for a locked door', async () => {
    await setup([
      makeLock({
        state: {
          name: 'Door Locked',
          materialIcon: 'lock',
          availableTransitions: [{ action: 'UNLOCK', label: 'Unlock' }],
          updatableFields: [],
        },
      }),
    ]);
    expect(compiled.textContent).toContain('Unlock');
  });

  // ── Multiple devices / locations ──────────────────────────────────────────

  it('renders devices grouped under their location heading', async () => {
    await setup([
      makeLight({ location: 'Living Room' }),
      makeFan({ uuid: 'fan-2', location: 'Bedroom' }),
    ]);
    const headings = compiled.querySelectorAll('h1');
    const headingTexts = Array.from(headings).map(h => h.textContent?.trim());
    expect(headingTexts).toContain('Living Room');
    expect(headingTexts).toContain('Bedroom');
  });

  it('renders multiple devices in the same location', async () => {
    await setup([
      makeLight({ uuid: 'l1', name: 'Light A', location: 'Office' }),
      makeFan({ uuid: 'f1', name: 'Fan A', location: 'Office' }),
    ]);
    expect(compiled.textContent).toContain('Light A');
    expect(compiled.textContent).toContain('Fan A');
  });

  // ── getRGBString ──────────────────────────────────────────────────────────

  it('getRGBString returns correct CSS value for white', async () => {
    await setup([]);
    const component = fixture.componentInstance as any;
    expect(component.getRGBString([255, 255, 255])).toBe('rgb(255, 255, 255)');
  });

  it('getRGBString returns black for null color', async () => {
    await setup([]);
    const component = fixture.componentInstance as any;
    expect(component.getRGBString(null)).toBe('black');
  });
});
