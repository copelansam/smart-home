import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private apiUrl = 'http://localhost:8080/api/test'; // Verify this matches your Java @GetMapping

  constructor(private http: HttpClient) {}

  getBackendData(): Observable<any> {
    return this.http.get(this.apiUrl);
  }
}
