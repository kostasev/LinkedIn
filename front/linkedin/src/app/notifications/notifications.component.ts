import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {
  notifications = [ {name: 'kostas', surname: 'evangelou', iduser: 1, idpost: 2 , action: 'liked' , seen: false},
    {name: 'kostas', surname: 'evangelou', iduser: 1, idpost: 2 , action: 'commented' , seen: true}];

  connections = [ {name: 'kostas', surname: 'evangelou', iduser: 1},
    {name: 'kostas2', surname: 'evangelou2', iduser: 2}];
  constructor() { }

  ngOnInit() {
  }

}
