import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DeviceService } from './services/device.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule], // Removed FormsModule since we have no inputs now
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent implements OnInit {
  devices: any[] = [];

  constructor(private deviceService: DeviceService) {}

  ngOnInit() {
    this.refreshList();
  }

  refreshList() {
    this.deviceService.getDevices().subscribe({
      next: (data: any[]) => {
        this.devices = data;
        console.log('Devices refreshed:', this.devices);
      },
      error: (err: any) => console.error('Failed to load devices', err)
    });
  }
}
