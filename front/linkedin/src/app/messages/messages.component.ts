import { Component, OnInit } from '@angular/core';
import {AuthService} from '../auth.service';
import {formatDate} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.css']
})
export class MessagesComponent implements OnInit {
  public myId;
  chatid;
  newmessage: string;
  msgUsers = [];
  par ;
  messages = [];
  chatname;
  constructor(private _auth: AuthService,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.par = params['id'];
    });
    this.myId = localStorage.getItem('id');
    this.getChats();
  }

  private getChats() {
    const token = {};
    token['id'] = 0;
    token['token'] = localStorage.getItem('token');
    this._auth.getChats(token)
      .subscribe(res => {
          console.log(res);
          this.msgUsers = res;
          this.makeDateVisible();
          if (this.par == 0) {
            this.getChatById(this.msgUsers[0].iduser);
          } else {
            this.getChatById(this.par);
          }
        },
        err => console.log(err));
  }

  private makeDateVisible() {
    for (const key of this.msgUsers) {
      key['datetime'] = formatDate(key['datetime'], 'yyyy-MM-dd HH:mm:ss', 'en');
    }
  }

  getChatById(iduser) {
    const token = {};
    token['id'] = iduser;
    token['token'] = localStorage.getItem('token');
    this._auth.getChat(token)
      .subscribe(res => {
          console.log('by');
          console.log(res);
          this.getChatId(iduser);
          this.messages = res;
        },
        err => console.log(err));
  }

  getChatId(iduser) {
    const token = {};
    token['id'] = iduser;
    token['token'] = localStorage.getItem('token');
    this._auth.getChatId(token)
      .subscribe(res => {
          console.log('id');
          console.log(res);
          this.chatid = res['id'];
          this.chatname = res['name'] + ' ' + res['surname'];
        },
        err => console.log(err));
  }

  sendMessage(idchat) {
    const token = {};
    token['token'] = localStorage.getItem('token');
    token['iduser'] = idchat;
    token['text'] = this.newmessage;
    console.log(token);
    this._auth.sendMsg(token)
      .subscribe(res => {
          console.log(res);
          delete token['token'];
          const msg = { iduser:  parseInt(localStorage.getItem('id'), 10 ), text: token['text']};
          this.messages.push(msg);
          this.newmessage = '';
        },
        err => console.log(err));
  }
}
