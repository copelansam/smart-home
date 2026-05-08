import { RxStomp } from '@stomp/rx-stomp';
import { Injectable } from '@angular/core';
import { map, filter } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class WebSocketService {

  private rxStomp = new RxStomp();

  constructor() {
    this.rxStomp.configure({
      brokerURL: 'ws://localhost:8080/ws',
      reconnectDelay: 5000,
      debug: (str) => console.log(str),
    });

    this.rxStomp.activate();
  }

  private raw$ = this.rxStomp.watch('/topic/devices').pipe(
    map(message => JSON.parse(message.body))
  );

private deleteRaw$  = this.rxStomp.watch('/topic/devices/delete').pipe(
                     map(message => message.body)
                   );

  deviceUpdates$ = this.raw$.pipe(
    filter(msg => msg.type !== 'DELETE')
  );

 deviceDelete$ = this.deleteRaw$;
}
