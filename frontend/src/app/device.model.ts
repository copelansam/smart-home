type UUID = string;
type RGB = number[];
type FanSpeed = 'OFF' | 'LOW' | 'MEDIUM' | 'HIGH';
type Temperature = number;
export type DeviceType = 'LIGHT' | 'FAN' | 'DOORLOCK' | 'THERMOSTAT'

// Transitions
export interface ITransition{
  action: string;
  label: string;
  }

// States
export interface IState{

  name: string;
  availableTransitions: ITransition[];
  updatableFields: ITransition[];

  }

// Devices
export interface SmartDeviceBase{
  uuid: UUID;
  name: string;
  location: string;
  deviceType: DeviceType;
  isOn: boolean;
  state: IState;
  attributes: Record<string, any>;
  availableTransitions: Array<{ action: string, label: string }>;
  updatableFields: Array< { action: string, label: string}>;
  }

export interface SmartLight extends SmartDeviceBase{
  attributes:{
    brightnessPercentage: number;
    color: RGB;
    };
  }

export interface SmartLock extends SmartDeviceBase{
  attributes:{};
  }

export interface SmartFan extends SmartDeviceBase{
  attributes:{
    speed: FanSpeed;
    };
  }

export interface SmartThermostat extends SmartDeviceBase{
  attributes:{
    ambientTemperature: Temperature;
    desiredTemperature: Temperature;
    mode: string;
    };
  }

export type SmartDevice = SmartFan | SmartLight | SmartLock | SmartThermostat;

export const FAN_SPEEDS = [
  { label: 'Low', value: 'LOW'},
  { label: 'Medium', value: 'MEDIUM'},
  { label: 'High', value: 'HIGH'}
  ];

export const THERMOSTAT_MODES = [
  { label: 'Auto', value: 'AUTO'},
  { label: 'Cool', value: 'COOL'},
  { label: 'Heat', value: 'HEAT'}
  ];

// Device Logs

export interface DeviceLog{

  logId: number;
  deviceUuid: UUID;
  message: string;
  event: string;
  timestamp: string | Date;
  }

