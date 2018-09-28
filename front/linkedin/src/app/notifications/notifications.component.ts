import {Component, Input, OnInit, Output} from '@angular/core';
import {AuthService} from '../auth.service';
import * as glb from '../navbar/navbar.component';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {
  notifications = [];
  connections = [];
  constructor(private _auth: AuthService) { }

  ngOnInit() {
    this.getConRequests();
    this.getNotifications();
    localStorage.setItem('notif', String(false));
  }


  private getConRequests() {
    const token = {};
    token['id'] = 0;
    token['token'] = localStorage.getItem('token');
    this._auth.getRequests(token)
      .subscribe(res => {
          console.log(res);
          this.connections = res;
        },
        err => console.log(err));
  }

  acceptRequest(con) {
    const token = {};
    token['id'] = con.iduser1;
    token['token'] = localStorage.getItem('token');
    this._auth.accRequest(token)
      .subscribe(res => {
          console.log(res);
          this.connections.splice(this.connections.indexOf(con));
        },
        err => console.log(err));
  }

  delRequest(con) {
    const token = {};
    token['id'] = con.iduser1;
    token['token'] = localStorage.getItem('token');
    this._auth.deleteRequest(token)
      .subscribe(res => {
          console.log(res);
          this.connections.splice(this.connections.indexOf(con));
        },
        err => console.log(err));
  }

  private getNotifications() {
    const token = {};
    token['id'] = 0;
    token['token'] = localStorage.getItem('token');
    this._auth.getNotifs(token)
      .subscribe(res => {
          console.log(res);
          this.notifications = res;
          this.setSeen(token);
        },
        err => console.log(err));
  }

  setSeen(token) {
    this._auth.setSeen(token)
      .subscribe(res => {
          console.log(res);
        },
        err => console.log(err));
  }
}
