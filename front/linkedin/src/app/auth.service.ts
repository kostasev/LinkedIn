import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private LoginUrl   = 'http://localhost:8080/linkedin/api/user/login';
  private SignupUrl  = 'http://localhost:8080/linkedin/api/user/signup';
  private ChangePss  = 'http://localhost:8080/linkedin/api/user/changepass';
  private GetInfo    = 'http://localhost:8080/linkedin/api/user/myinfo';
  private UpdateUser = 'http://localhost:8080/linkedin/api/user/update';
  private GetSkills  = 'http://localhost:8080/linkedin/api/profile/getskills';
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
    localStorage.removeItem('id');
  }

  changePass(user) {
    return this.http.post<any>(this.ChangePss, user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  getMyInfo(token) {
    return this.http.post<any>(this.GetInfo, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }
  updateUser(user) {
    console.log(user);
    user['token'] = localStorage.getItem('token');
    return this.http.post<any>(this.UpdateUser, user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  getUserSkills(user) {
    return this.http.post<any>(this.GetSkills, user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }
}
