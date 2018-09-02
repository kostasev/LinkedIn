import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css']
})
export class CommentsComponent implements OnInit {
  comments = [{name: 'nik', surname: 'evangelou', comment: 'ble ble ble', time: '12-2-2018 13:30', iduser: 1, idpost: 2}];
  constructor() { }

  ngOnInit() {
  }

}
