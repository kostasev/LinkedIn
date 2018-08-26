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
  private GetInfo    = 'http://localhost:8080/linkedin/api/user/info';
  private UpdateUser = 'http://localhost:8080/linkedin/api/user/update';
  private GetSkills  = 'http://localhost:8080/linkedin/api/profile/getskills';
  private DeleteSkillUrl = 'http://localhost:8080/linkedin/api/profile/deleteskill';
  private NewSkillUrl = 'http://localhost:8080/linkedin/api/profile/addskill';
  private UpdateSkillUrl = 'http://localhost:8080/linkedin/api/profile/updateskill';
  private ConnectedUrl = 'http://localhost:8080/linkedin/api/profile/isconnected';
  private ConnectUrl = 'http://localhost:8080/linkedin/api/network/connect';
  private DelConnectUrl = 'http://localhost:8080/linkedin/api/network/delconnect';
  private GetConnections = 'http://localhost:8080/linkedin/api/network/connections';
  private GetSearch = 'http://localhost:8080/linkedin/api/network/search';
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

  getInfoById(user) {
    return this.http.post<any>(this.GetInfo, user, {
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

  deleteSkillById(user) {
    return this.http.post(this.DeleteSkillUrl, user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  newSkill(skill) {
    return this.http.post(this.NewSkillUrl, skill, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  changeSkill(skill) {
    return this.http.post(this.UpdateSkillUrl, skill, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  isConnected(user) {
    return this.http.post(this.ConnectedUrl, user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  connect(user) {
    return this.http.post(this.ConnectUrl, user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  delConnect(user) {
    return this.http.post(this.DelConnectUrl, user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  getUserConnections(user) {
    return this.http.post<any>(this.GetConnections, user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  userSearch(user) {
    return this.http.post<any>(this.GetSearch, user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }
}
