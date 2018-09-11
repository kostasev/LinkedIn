import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css']
})
export class CommentsComponent implements OnInit {
  comments = [{name: 'nik', surname: 'evangelou', comment: 'ble ble ble', time: '12-2-2018 13:30', iduser: 1, idpost: 2}];
  constructor( private _auth: AuthService) { }
  @Input()
  idpost: string;
  ngOnInit() {
    this.getComments(this.idpost);
  }

  getComments (postid) {
    const token = {};
    token['idpost'] = postid;
    token['token'] = localStorage.getItem('token');
    this._auth.loadComments(token).subscribe(
      res => {console.log(res);
        this.comments = res;
      }
      ,
      err => console.log(err)
    );
  }

}
