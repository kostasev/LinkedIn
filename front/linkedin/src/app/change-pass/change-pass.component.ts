import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-change-pass',
  templateUrl: './change-pass.component.html',
  styleUrls: ['./change-pass.component.css']
})
export class ChangePassComponent implements OnInit {
  user = {};
  public successMsg;
  public errorMsg;
  constructor(private _auth: AuthService) { }

  ngOnInit() {
  }
  UpdatePass() {
    this.user['token'] = localStorage.getItem('token');
    if ( this.user['pass'] === this.user['repass']) {
      delete this.user['repass'];
    } else {
      this.errorMsg = 'Password repeat doesn\'t match!';
      return ;
    }
    console.log(this.user);
    this._auth.changePass(this.user)
      .subscribe( res => {
        console.log(res);
        this.successMsg = 'Password Changed !';
      },
      err => {
        console.log(err);
        this.errorMsg = 'Invalid Password';
      });
  }

}
