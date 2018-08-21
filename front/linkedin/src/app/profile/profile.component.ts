import { Component, OnInit } from '@angular/core';
import {AuthService} from '../auth.service';
import {ImageService} from '../image.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user = {};

  selectedFile: File = null;
  imageToShow: any;
  isImageLoading: boolean;
  constructor(private _auth: AuthService,
          private imageService: ImageService) { }

  ngOnInit() {
    this.user['token'] = localStorage.getItem('token');
    this._auth.getMyInfo(this.user)
      .subscribe(res => this.user = res );
    console.log(this.user);
    this.getImageFromService();
  }

  isme() {
    return (this.user['id'] == localStorage.getItem('id'));
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
    this.isImageLoading = true;
    this.imageService.getProfileImage(localStorage.getItem('id')).subscribe(data => {
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
    fd.append('image', this.selectedFile, this.selectedFile.name);
    this.imageService.onUpload(fd).subscribe(
      res => console.log(res),
      err => console.log(err)
    );
  }
}
