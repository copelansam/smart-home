import { RxStomp } from '@stomp/rx-stomp';
import { Injectable } from '@angular/core';
import { map, filter } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })

export class WebSocketService{

  private rxStomp = new RxStomp();

  constructor() {
      this.rxStomp.configure({
        brokerURL: 'ws://localhost:8080/ws/websocket',
        reconnectDelay: 5000,
        debug: (str) => console.log(str),
      });

      this.rxStomp.activate();
    }

    private raw$ = this.rxStomp.watch('/topic/devices').pipe(
      map(message => JSON.parse(message.body))
    );

   // UPSERT + RESET stream
    deviceUpdates$ = this.raw$.pipe(
      filter(msg => msg.type !== 'DELETE')
    );

    // DELETE stream
    deviceDelete$ = this.raw$.pipe(
      filter(msg => msg.type === 'DELETE'),
      map(msg => msg.uuid)
    );

  }
