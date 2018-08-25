import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {formatDate} from '@angular/common';

@Component({
  selector: 'app-skill',
  templateUrl: './skill.component.html',
  styleUrls: ['./skill.component.css']
})
export class SkillComponent implements OnInit {
  skilldel = {};
  editSkill = {};
  closeResult: string;
  private user = {};
  public skills = [];
  @Input()
  type: number;
  @Input()
  is_me: boolean;
  @Input()
  id: number;
  constructor(private _auth: AuthService,
              private modalService: NgbModal) { }

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

  deleteSkill(skill, id) {
    this.skills.splice(id, 1);
    this.skilldel['id'] = skill;
    this.skilldel['token'] = localStorage.getItem('token');
    this._auth.deleteSkillById(this.skilldel).subscribe(
      res => console.log(res),
      error => console.log(error)
    );
  }


  open(content, skill) {
    this.editSkill = skill;
    this.editSkill['datetime_start'] = formatDate( this.editSkill['datetime_start'], 'yyyy-MM-dd', 'en' );
    this.editSkill['datetime_end'] = formatDate( this.editSkill['datetime_end'], 'yyyy-MM-dd', 'en' );
    console.log(this.editSkill);
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return  `with: ${reason}`;
    }
  }

  updateSkill() {
    this.editSkill['token'] = localStorage.getItem('token');
    this._auth.changeSkill(this.editSkill).subscribe(
      res => {console.log(res);
      }
      ,
      err => console.log(err)
    );
  }
}
