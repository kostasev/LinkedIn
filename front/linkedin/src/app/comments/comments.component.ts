import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';
import {formatDate} from '@angular/common';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css']
})
export class CommentsComponent implements OnInit {
  comments = [];
  newComment = {};
  constructor( private _auth: AuthService) { }
  @Input()
  idpost: number;
  ngOnInit() {
    this.getComments(this.idpost);
  }

  getComments (postid) {
    const token = {};
    token['id'] = postid;
    token['token'] = localStorage.getItem('token');
    this._auth.loadComments(token).subscribe(
      res => {console.log(res);
        this.comments = res;
        this.makeDateVisible( );
      }
      ,
      err => console.log(err)
    );
  }

  private makeDateVisible( ) {
    for (const key of this.comments) {
      key['datetime'] = formatDate( key['datetime'], 'yyyy-MM-dd HH:mm:ss', 'en' );

    }
  }

  sendComment(postid) {
    this.newComment['iduser'] = postid;
    this.newComment['token'] = localStorage.getItem('token');
    console.log(this.newComment);
    this._auth.createComment(this.newComment).subscribe(
      res => {console.log(res);
        this.getComments(postid);
        this.newComment['post'] = '';
      },
      err => console.log(err)
    );
  }
}
