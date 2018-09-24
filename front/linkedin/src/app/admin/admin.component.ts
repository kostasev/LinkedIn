import { Component, OnInit } from '@angular/core';
import {AuthService} from '../auth.service';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {formatDate} from '@angular/common';
import { saveAs } from 'file-saver/FileSaver';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  user = {};
  users = [];
  closeResult: string;
  newUser = {};
  constructor(private _auth: AuthService,
              private modalService: NgbModal) { }

  ngOnInit() {
    this.getUsers();
  }

  getUsers() {
    this.user['token'] = localStorage.getItem('token');
    this.user['id'] = localStorage.getItem('id');
    this._auth.getUsers(this.user)
      .subscribe(res =>
          this.users = res,
        err => console.log(err));
  }

  open(content, user) {
    this.newUser = user;
    this.newUser['birthday'] = formatDate( this.newUser['birthday'], 'yyyy-MM-dd', 'en' );
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

  /*getCSV() {
    const userlist = [];
    const token = {};
    this.users.forEach( function (arrayItem) {
      if ( arrayItem['checked'] ) {
        userlist.push({iduser: arrayItem['iduser'] });
      }
    });
    token['token'] = localStorage.getItem('token');
    token['id'] = 0;
    this._auth.getXml(token)
      .subscribe(res => {
        const blob = new Blob([res], {type: 'text/xml'});
        saveAs(blob, 'users.xml');
        },
        err => console.log(err));
  }*/

  getCSV() {
    const token = {};
    token['token'] = localStorage.getItem('token');
    token['id'] = 0;
    this._auth.getXml(token).subscribe(data => {
      this.createImageFromBlob(data);
    }, error => {
      console.log(error);
    });
  }

  createImageFromBlob(image: Blob) {
    saveAs( image, 'users.xml');
  }
}
