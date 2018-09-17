import { Component, OnInit } from '@angular/core';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from '../auth.service';
import {computeStyle} from '@angular/animations/browser/src/util';

@Component({
  selector: 'app-jobs',
  templateUrl: './jobs.component.html',
  styleUrls: ['./jobs.component.css']
})
export class JobsComponent implements OnInit {
  closeResult: string;
  editjb = {};
  newjb = {};
  skills = [];
  _job = {};
  applicants = [];
  public MyJobs = [{
    title: 'C1 Developer',
    description: 'Young and wild and free c developer with a car and available for breakable swift'
  }];
  public NetJobs = [{
    title: 'C2 Developer',
    description: 'Young and wild and free c developer with a car and available for breakable swift'
  }];
  public SugJobs = [{
    title: 'C3 Developer',
    description: 'Young and wild and free c developer with a car and available for breakable swift'
  }];

  constructor(
    private modalService: NgbModal,
    private _auth: AuthService
  ) {
  }

  ngOnInit() {
    this.newjb['visible'] = 'public';
    this.getMyJob(4);
  }

  open(content, type) {
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
      return `with: ${reason}`;
    }
  }

  newJob() {
    console.log(this.newjb);
    this.newjb['token'] = localStorage.getItem('token');
    this._auth.postJob(this.newjb)
      .subscribe(res => {
        console.log(res);
        this._auth.getMyJobs(4);
      });
  }

  private getMyJob(number: number) {
    this._auth.getMyJobs(number)
      .subscribe(res => {
        console.log(res);
        this.MyJobs = res;
      });
  }

  getSkills(job) {
    this._job = job;
    this._auth.getskills(job.idjobs)
      .subscribe(res => {
        console.log(res);
        this.skills = res;
      });
  }

  getAppl(job) {
    this._job = job;
    this._auth.getApplicants(job.idjobs)
      .subscribe(res => {
        console.log(res);
        this.applicants = res;
      });
  }

  deleteJob(num){
    const token = {};
    token['id'] = num;
    token['token'] = localStorage.getItem('token');
    this._auth.getDeleteJob(token)
      .subscribe(res => {
        console.log(res);
      },
        err => console.log(err));
  }
}
