import { Component, OnInit } from '@angular/core';
import {AuthService} from '../auth.service';
import {formatDate} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {from, interval, pipe, timer} from 'rxjs';
import {concatMap, map, startWith, switchMap} from 'rxjs/operators';
import {promise} from 'selenium-webdriver';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {element} from 'protractor';

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
  test;
  curchat: number;
  lastdate;
  constructor(private _auth: AuthService,
              private route: ActivatedRoute,
              private http: HttpClient) { }


  ngOnInit() {
    this.route.params.subscribe(params => {
      this.par = params['id'];
    });
    this.myId = localStorage.getItem('id');
    this.getChats();
    interval(1000)
      .pipe(
        startWith(0),
        switchMap(() => this._auth.getMsg(this.chatid , this.lastdate))
      )
      .subscribe(res => {
        if (res['answer'] != 'No') {
          console.log(res);
          this.messages.push(res[0]);
          this.lastdate = res[0]['datetime'];
          this.lastdate = formatDate(this.lastdate, 'yyyy-MM-dd HH:mm:ss', 'en');
          console.log('on success' + this.lastdate);
        }
      }, error1 => console.log( error1));
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
          this.lastdate = this.messages[this.messages.length - 1]['datetime'];
          this.lastdate = formatDate(this.lastdate, 'yyyy-MM-dd HH:mm:ss', 'en');
          console.log('last date' + this.lastdate);
        },
        err => console.log(err));
  }

  getChatId(iduser) {
    const token = {};
    token['id'] = iduser;
    token['token'] = localStorage.getItem('token');
    this._auth.getChatId(token)
      .subscribe(res => {
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
          this.newmessage = '';
        },
        err => console.log(err));
  }
}
