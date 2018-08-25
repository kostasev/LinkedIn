import { Component, OnInit } from '@angular/core';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-network',
  templateUrl: './network.component.html',
  styleUrls: ['./network.component.css']
})
export class NetworkComponent implements OnInit {
  connections = [];
  user = {};
  noConns = false;
  constructor(private _auth: AuthService) { }

  ngOnInit() {
    this.getConnections();
  }

  getConnections() {
    this.user['token'] = localStorage.getItem('token');
    this.user['id'] = localStorage.getItem('id');
    this._auth.getUserConnections(this.user)
      .subscribe(res =>
        this.connections = res,
        err => this.noConns = true);
  }

}
