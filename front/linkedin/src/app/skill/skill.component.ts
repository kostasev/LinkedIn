import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-skill',
  templateUrl: './skill.component.html',
  styleUrls: ['./skill.component.css']
})
export class SkillComponent implements OnInit {
  private user = {};
  public skills = [];
  @Input()
  type: number;
  @Input()
  isme: boolean;
  constructor(private _auth: AuthService) { }

  ngOnInit() {
    this.getSkills();
  }

  getSkills() {
    this.user['token'] = localStorage.getItem('token');
    this.user['id'] = localStorage.getItem('id');
    this._auth.getUserSkills(this.user)
      .subscribe(res => this.skills = res );
  }
}
