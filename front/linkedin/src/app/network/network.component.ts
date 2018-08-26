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
  search: string;
  searchUser = {};
  noRes = false;
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

  getSearch() {
    console.log(this.search);
    const words = this.search.split(' ');
    console.log(words);
    if (words.length == 0 ) { return; }
    if (words.length == 1 ) {
      this.searchUser['name'] = words[0];
      this.searchUser['surname'] = 'default';
    } else {
      this.searchUser['name'] = words[0];
      this.searchUser['surname'] = words[1];
    }
    this.searchUser['token'] = localStorage.getItem('token');
    this._auth.userSearch(this.searchUser)
      .subscribe(res => {
          this.connections = res,
            this.noRes = false;
        },
        err => this.noRes = true);
  }

}
