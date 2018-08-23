import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-skill',
  templateUrl: './skill.component.html',
  styleUrls: ['./skill.component.css']
})
export class SkillComponent implements OnInit {
  skilldel = {};
  private user = {};
  public skills = [];
  @Input()
  type: number;
  @Input()
  is_me: boolean;
  @Input()
  id: number;
  constructor(private _auth: AuthService) { }

  ngOnInit() {
    this.getSkills();
  }

  getSkills() {
    this.user['token'] = localStorage.getItem('token');
    this.user['id'] = this.id;
    this._auth.getUserSkills(this.user)
      .subscribe(res => {
        this.skills = res;
        console.log(this.skills);
      } );
  }

  deleteSkill(id) {
    this.skilldel['id'] = id;
    this.skilldel['token'] = localStorage.getItem('token');
    this._auth.deleteSkillById(this.skilldel).subscribe(
      res => console.log(res),
      error => console.log(error)
    );
  }
}
