import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import { Client,over } from 'stompjs';
import { BehaviorSubject } from 'rxjs';


@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private stompClient: Client | null = null;
  private newEmployeeSubject = new BehaviorSubject<any | null>(null);
  newEmployee$ = this.newEmployeeSubject.asObservable();

  connect() {
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = over(socket);

    this.stompClient.connect({}, () => {
      this.stompClient?.subscribe('/topic/new-employee', (msg) => {
        if (msg.body) {
          this.newEmployeeSubject.next(JSON.parse(msg.body));
        }
      });
    });
  }
}