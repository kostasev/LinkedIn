import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.css']
})
export class MessagesComponent implements OnInit {
  public myId;
  msgUsers = [
    { name: 'kostas' , surname: 'evaggelou', id: 1 },
    { name: 'nik' , surname: 'evaggelou', id: 2 },
    { name: 'lid' , surname: 'evaggelou', id: 3 },
    { name: 'kostas' , surname: 'evaggelou', id: 1 },
    { name: 'nik' , surname: 'evaggelou', id: 2 },
    { name: 'lid' , surname: 'evaggelou', id: 3 },
    { name: 'kostas' , surname: 'evaggelou', id: 1 },
    { name: 'nik' , surname: 'evaggelou', id: 2 },
    { name: 'lid' , surname: 'evaggelou', id: 3 },
    { name: 'kostas' , surname: 'evaggelou', id: 1 },
    { name: 'nik' , surname: 'evaggelou', id: 2 },
    { name: 'lid' , surname: 'evaggelou', id: 3 },
    { name: 'kostas' , surname: 'evaggelou', id: 1 },
    { name: 'nik' , surname: 'evaggelou', id: 2 },
    { name: 'lid' , surname: 'evaggelou', id: 3 },
    { name: 'kostas' , surname: 'evaggelou', id: 1 },
    { name: 'nik' , surname: 'evaggelou', id: 2 },
    { name: 'lid' , surname: 'evaggelou', id: 3 }
  ]

  messages = [ {id: 1 , text: 'mpoale amsdmasd asn a fa afa fa fasdfasd '},
    {id: 2 , text: 'sd asn a fa afa fa fasdfasd '},
    {id: 1 , text: 'mpoale amsdmasd asn a fa afa fa fasdfasd '},
    {id: 2 , text: 'sd asn a fa afa fa fasdfasd '},
    {id: 1 , text: 'mpoale amsdmasd asn a fa afa fa fasdfasd '},
    {id: 2 , text: 'sd asn a fa afa fa fasdfasd '},
    {id: 1 , text: 'mpoale amsdmasd asn a fa afa fa fasdfasd '},
    {id: 2 , text: 'sd asn a fa afa fa fasdfasd '}];
  constructor() { }

  ngOnInit() {
    this.myId = localStorage.getItem('id');
  }

}
