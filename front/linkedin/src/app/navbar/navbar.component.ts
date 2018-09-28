import {Component, OnInit, ɵQueryValueType} from '@angular/core';
import { AuthService } from '../auth.service';
import {interval} from 'rxjs';
import {startWith, switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  public notif = false;
  constructor(public _authService: AuthService) { }

  public setnotif(b: boolean) {
    this.notif = b;
    console.log('the value of notif ' + this.notif);
  }

  ngOnInit() {
    const token = {};
    token['token'] = localStorage.getItem('token');
    token['id'] = 0;
    interval(1000)
      .pipe(
        startWith(0),
        switchMap(() => this._authService.getNotif(token))
      )
      .subscribe(res => {
        if ( res['answer'] == 'yes' ) {
          localStorage.setItem('notif', String(true));
          this.setnotif(true);
          localStorage.setItem('notif', String(false));
        } else {
          localStorage.setItem('notif', String(false));
          this.setnotif(false);
        }
      });
  }

}
