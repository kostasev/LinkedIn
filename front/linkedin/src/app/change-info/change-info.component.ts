import { Component, OnInit } from '@angular/core';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-change-info',
  templateUrl: './change-info.component.html',
  styleUrls: ['./change-info.component.css']
})
export class ChangeInfoComponent implements OnInit {
  user = {};
  constructor(private _auth: AuthService) {
  }

  ngOnInit() {
    this.user['token'] = localStorage.getItem('token');
    this.user['id'] = localStorage.getItem('id');
    this._auth.getInfoById(this.user)
      .subscribe(res => this.user = res );
    console.log(this.user);
  }

  UpdateUser() {
    this._auth.updateUser(this.user)
      .subscribe(res => console.log(res),
      err => console.log(err));
  }
}
