import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css']
})
export class PostsComponent implements OnInit {
  posts = [{name: 'kostas', surname: 'evangelou', post: 'mpla mpla mpla mpla', time: '12-2-2018 13:30', iduser: 1, likes: 50, idpost: 2 },
    {name: 'kostas', surname: 'evangelou', post: 'mpla mpla mpla mpla', time: '12-2-2018 13:30', iduser: 1 , likes: 5, idpost: 12}];
  constructor() { }

  ngOnInit() {
  }

}
