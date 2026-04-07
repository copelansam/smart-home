import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {
  // Since /api/devices works for GET, we use it as the base
  private apiUrl = 'http://localhost:8080/api/devices';

  constructor(private http: HttpClient) {}

  getDevices(): Observable<any[]> {
    // GET http://localhost:8080/api/devices
    return this.http.get<any[]>(this.apiUrl);
  }

  createDevice(name: string): Observable<any> {
    // POST http://localhost:8080/api/devices
    // Note: If your POST actually requires the "/create-device" suffix,
    // change this to: this.apiUrl + '/create-device'
    return this.http.post(this.apiUrl, { name: name });
  }
}
