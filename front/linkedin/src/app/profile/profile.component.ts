import { Component, OnInit } from '@angular/core';
import {AuthService} from '../auth.service';
import {ImageService} from '../image.service';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user = {};
  addSkill = {};

  selectedFile: File = null;
  imageToShow: any;
  isImageLoading: boolean;
  isMe: boolean;
  closeResult: string;
  connected: boolean;
  model;
  conMessage = '+ Add Connection';
  delMessage = 'Delete Connection';
  usr = {};
  constructor(private _auth: AuthService,
              private imageService: ImageService,
              private route: ActivatedRoute,
              private modalService: NgbModal,
              private router: Router) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.user['id'] = params['id'];
    });
    this.user['token'] = localStorage.getItem('token');
    this._auth.getInfoById(this.user)
      .subscribe(res => this.user = res );
    console.log(this.user);
    this.getImageFromService();
    this.isme();
    this.checkConnection();
  }


  delConnection() {
    this.route.params.subscribe(params => {
      this.usr['id'] = params['id'];
    });
    this.usr['token'] = localStorage.getItem('token');
    this._auth.delConnect(this.usr).subscribe(
      res => {
        console.log(res);
        this.connected = false;
      },
      err => console.log(err)
    );
  }

  addConnection() {
    this.route.params.subscribe(params => {
      this.usr['id'] = params['id'];
    });
    this.usr['token'] = localStorage.getItem('token');
    this._auth.connect(this.usr).subscribe(
      res => {
        console.log(res);
        this.conMessage = 'Request Sent';
      },
          err => console.log(err)
    );
  }

  checkConnection() {
    console.log(this.user);
    this._auth.isConnected(this.user).subscribe(
      res => {
        console.log(res);
        this.connected = res['connected'];
      },
      err => console.log(err)
    );
  }

  isme() {
    this.isMe = ((this.user['id'] == localStorage.getItem('id')) || (this.user['id'] == 0));
  }

  createImageFromBlob(image: Blob) {
    const reader = new FileReader();
    reader.addEventListener('load', () => {
      this.imageToShow = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

  getImageFromService() {
    let id;
    this.isImageLoading = true;
    if (this.user['id'] == 0 ) { id = localStorage.getItem('id'); } else { id = this.user['id']; }
    this.imageService.getProfileImage(id).subscribe(data => {
      this.createImageFromBlob(data);
      this.isImageLoading = false;
    }, error => {
      this.isImageLoading = true;
      console.log(error);
    });
  }

  onFileSelect(event) {
    console.log(event);
    this.selectedFile = <File>event.target.files[0];
    const fd = new FormData();
    fd.append('file', this.selectedFile, this.selectedFile.name);
    fd.append('token', localStorage.getItem('token'));
    this.imageService.onUpload(fd).subscribe(
      res => {
        this.getImageFromService();
        console.log(res);
        },
      err => console.log(err)
    );
  }

  open(content, type) {
    this.addSkill['type'] = type;
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

  sendSkill() {
    console.log(this.addSkill);
    this.addSkill['token'] = localStorage.getItem('token');
    this.addSkill['idskill'] = 0;
    this._auth.newSkill(this.addSkill).subscribe(
      res => {console.log(res);
        location.reload();
      }
      ,
      err => console.log(err)
    );
  }
}
