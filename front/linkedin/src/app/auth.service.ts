import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private LoginUrl = 'http://localhost:8080/linkedin/api/user/login';
  private SignupUrl = 'http://localhost:8080/linkedin/api/user/signup';
  constructor(private http: HttpClient) { }

  loginUser(user) {
    return this.http.post<any>(this.LoginUrl, user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  SignUser(userCr) {
    return this.http.post<any>(this.SignupUrl, userCr, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  loggedIn() {
    return !!localStorage.getItem('token');
  }

  logoutUser() {
    localStorage.removeItem('token');
  }
}
