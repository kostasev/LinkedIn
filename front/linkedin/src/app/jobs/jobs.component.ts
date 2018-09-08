import { Component, OnInit } from '@angular/core';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-jobs',
  templateUrl: './jobs.component.html',
  styleUrls: ['./jobs.component.css']
})
export class JobsComponent implements OnInit {
  closeResult: string;
  editjb = {};
  public jobs = [
    {title: 'C Developer' , description: 'Young and wild and free c developer with a car and available for breakable swift' } ,
    {title: 'C2 Developer' , description: 'Young and wild and free c developer with aasdasdsda car and available for breakable swift' } ,
    {title: 'TCL Developer' , description: 'Young and wild and free c developer with a car assssssssndasdasdasdsdasda available for breakable swift' } ,
    {title: 'C# Developer' , description: 'Young and wild and free c develorrper with a car anbreakable swift'} ] ;
  constructor(
    private modalService: NgbModal
  ) { }

  ngOnInit() {
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
      return  `with: ${reason}`;
    }
  }

}
