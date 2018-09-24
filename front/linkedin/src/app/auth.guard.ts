import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService} from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor( private _authService: AuthService,
               private _router: Router ) { }
  canActivate(): boolean {
    if (this._authService.loggedIn()) {
      return true;
    } else if ( this._authService.isAdmin()) {
      this._router.navigate(['/admin']);
      return true;
    } else {
      this._router.navigate(['/register']);
      return false;
    }
  }
}
