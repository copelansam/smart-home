type UUID = string;
type RGB = number[];
type FanSpeed = 'OFF' | 'LOW' | 'MEDIUM' | 'HIGH';
type Temperature = number;
export type DeviceType = 'LIGHT' | 'FAN' | 'DOORLOCK' | 'THERMOSTAT'

// Transitions
export interface ITransition{
  action: string;
  }

// States
export interface IState{

  name: string;
  availableTransitions: ITransition[];

  }

// Devices
export interface SmartDeviceBase{
  uuid: UUID;
  name: string;
  location: string;
  deviceType: DeviceType;
  isOn: boolean;
  state: IState;
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

    };
  }

export type SmartDevice = SmartFan | SmartLight | SmartLock | SmartThermostat;
