import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.css']
})
export class MessagesComponent implements OnInit {
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
  constructor() { }

  ngOnInit() {
  }

}
