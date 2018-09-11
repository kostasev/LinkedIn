import { Component, OnInit } from '@angular/core';
import { AuthService} from '../auth.service';
import { ImageService } from '../image.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  user = {};
  imageToShow: any;
  isImageLoading: boolean;
  constructor(private _auth: AuthService,
              private imageService: ImageService) { }

  ngOnInit() {
    this.user['id'] = localStorage.getItem('id');
    this.user['token'] = localStorage.getItem('token');
    this._auth.getInfoById(this.user)
      .subscribe(res => this.user = res );
    this.getImageFromService();

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





}
