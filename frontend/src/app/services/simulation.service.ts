import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })

export class SimulationService{

  private http = inject(HttpClient);
  private simulationUrl = 'http://localhost:8080/api/simulation';

  /**
   * Resets all simulated devices back to factory state.
   * Backend returns 204 No Content.
   */
  factoryResetAllDevices(){
        return this.http.post<void>(`${this.simulationUrl}/reset`, null);
    }

  /**
   * Updates the global simulation speed multiplier.
   * Backend returns 204 No Content.
   */
    updateSimulationSpeed(timeMultiplier: number) {
      return this.http.post<void>(
        `${this.simulationUrl}/speed`,
        null,
        { params: { timeMultiplier } }
      );
    }

   /**
    * Updates ambient temperature for a given location.
    * Backend returns 204 No Content.
    */
    updateLocationTemperature(location: string, temperature: number) {
      return this.http.post<void>(
        `${this.simulationUrl}/location/temperature`,
        { location, temperature }
      );
    }
  }
