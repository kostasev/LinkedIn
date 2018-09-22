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
  private NewSkillUrl    = 'http://localhost:8080/linkedin/api/profile/addskill';
  private UpdateSkillUrl = 'http://localhost:8080/linkedin/api/profile/updateskill';
  private ConnectedUrl   = 'http://localhost:8080/linkedin/api/profile/isconnected';
  private ConnectUrl     = 'http://localhost:8080/linkedin/api/network/connect';
  private DelConnectUrl  = 'http://localhost:8080/linkedin/api/network/delconnect';
  private GetConnections = 'http://localhost:8080/linkedin/api/network/connections';
  private GetSearch      = 'http://localhost:8080/linkedin/api/network/search';
  private CreatePost     = 'http://localhost:8080/linkedin/api/post/create';
  private LikePost       = 'http://localhost:8080/linkedin/api/post/like';
  private GetComms       = 'http://localhost:8080/linkedin/api/post/comments';
  private PostComm       = 'http://localhost:8080/linkedin/api/post/newcomment';
  private GetPosts       = 'http://localhost:8080/linkedin/api/post/getposts';
  private GetLikes       = 'http://localhost:8080/linkedin/api/post/getlikes';
  private NewJob         = 'http://localhost:8080/linkedin/api/jobs/new';
  private GetMyJobs      = 'http://localhost:8080/linkedin/api/jobs/myjobs';
  private GetDescSkills  = 'http://localhost:8080/linkedin/api/jobs/skills';
  private GetJobAppl     = 'http://localhost:8080/linkedin/api/jobs/applicants';
  private DelJob         = 'http://localhost:8080/linkedin/api/jobs/delete';
  private GetNetJobs     = 'http://localhost:8080/linkedin/api/jobs/mynetwork';
  private ApplyJob       = 'http://localhost:8080/linkedin/api/jobs/apply';
  private GetSugJobs     = 'http://localhost:8080/linkedin/api/jobs/suggestions';
  private AccRequest     = 'http://localhost:8080/linkedin/api/notification/accept';
  private DelRequest     = 'http://localhost:8080/linkedin/api/notification/delete';
  private GetRequests    = 'http://localhost:8080/linkedin/api/notification/getall';
  private GetNotifs      = 'http://localhost:8080/linkedin/api/notification/getnotifs';
  private SetSeen        = 'http://localhost:8080/linkedin/api/notification/seen';
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

  createPost(post) {
    return this.http.post<any>(this.CreatePost, post, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  incrLikes(post) {
    return this.http.post<any>(this.LikePost, post, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  loadComments (token) {
    return this.http.post<any>(this.GetComms, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  createComment(post) {
    return this.http.post<any>(this.PostComm, post, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })});
  }

  GetMyPosts(token) {
    return this.http.post<any>(this.GetPosts, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  GetMyLikes(token) {
    return this.http.post<any>(this.GetLikes, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  postJob(job) {
    return this.http.post<any>(this.NewJob, job, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  getMyJobs(num) {
    const token = {};
    token['id'] = num;
    token['token'] = localStorage.getItem('token');
    return this.http.post<any>(this.GetMyJobs, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  getNetJobs(num) {
    const token = {};
    token['id'] = num;
    token['token'] = localStorage.getItem('token');
    return this.http.post<any>(this.GetNetJobs, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  getSugJobs(num) {
    const token = {};
    token['id'] = num;
    token['token'] = localStorage.getItem('token');
    return this.http.post<any>(this.GetSugJobs, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  getskills(num) {
    const token = {};
    token['id'] = num;
    token['token'] = localStorage.getItem('token');
    return this.http.post<any>(this.GetDescSkills, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  getApplicants(num) {
    const token = {};
    token['id'] = num;
    token['token'] = localStorage.getItem('token');
    return this.http.post<any>(this.GetJobAppl, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  getDeleteJob(token) {
    return this.http.post<any>(this.DelJob, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  doApply(token) {
    return this.http.post<any>(this.ApplyJob, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  accRequest(token) {
    return this.http.post<any>(this.AccRequest, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  deleteRequest(token) {
    return this.http.post<any>(this.DelRequest, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  getRequests(token) {
    return this.http.post<any>(this.GetRequests, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  getNotifs(token) {
    return this.http.post<any>(this.GetNotifs, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  setSeen(token) {
    return this.http.post<any>(this.SetSeen, token, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }
}
