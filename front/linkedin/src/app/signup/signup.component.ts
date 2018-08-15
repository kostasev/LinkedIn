import { Component, OnInit } from '@angular/core';
import { AuthService} from '../auth.service';
import { Router} from '@angular/router';


@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  userCr = {};
  public errorMsg;
  constructor(private _auth: AuthService,
              private _router: Router) { }

  ngOnInit() {
  }

  signUser() {
    const UserDate = new Date(this.userCr['birthday']);
    const today = new Date();
    const yyyy = today.getFullYear();
    console.log(this.userCr);
    this._auth.SignUser(this.userCr)
      .subscribe(
        res => {
          console.log(res);
          localStorage.setItem('token', res.token);
          this._router.navigate(['home']);
        },
        err => {
          if ( Object.keys(this.userCr).length < 6) {
            this.errorMsg = 'Fill in all fields';
          } else if ( this.userCr['pass'] !==  this.userCr['repass'] ) {
            this.errorMsg = 'Passwords doesn\'t match ';
          } else if ( yyyy - UserDate.getFullYear() < 18 ) {
            this.errorMsg = 'You should be at least 18 years old';
          } else {
            this.errorMsg = 'Email is being used';
          }
        }
      );
  }
}
