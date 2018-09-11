import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';
import {ImageService} from '../image.service';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css']
})
export class PostsComponent implements OnInit {
  posts = [
    {name: 'kostas', surname: 'evangelou',
    post: 'mpla mpla mpla mpla', datetime: '12-2-2018 13:30',
    iduser: 1, likes: 50, idpost: 2 },
    {name: 'kostas', surname: 'evangelou',
      post: 'mpla mpla mpla mpla', datetime: '12-2-2018 13:30',
      iduser: 1 , likes: 5, idpost: 12
    }];
  selectedFile: File = null;
  imageToShow: any;
  isImageLoading: boolean;
  newPost = {};
  @Input()
  name: string;
  @Input()
  surname: string;
  constructor(private _auth: AuthService,
              private imageService: ImageService) { }
  ngOnInit() {
    this.newPost['visible'] = 'public';
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
    id = localStorage.getItem('id');
    this.imageService.getProfileImage(id).subscribe(data => {
      this.createImageFromBlob(data);
      this.isImageLoading = false;
    }, error => {
      this.isImageLoading = true;
      console.log(error);
    });
  }

  sendPost() {
    let post;
    this.newPost['token'] = localStorage.getItem('token');
    this.newPost['iduser'] = localStorage.getItem('id');
    this.newPost['name'] = this.name;
    this.newPost['surname'] = this.surname;
    console.log(this.newPost);
    this._auth.createPost(this.newPost).subscribe(
      res => {console.log(res);
        this.newPost['idpost'] = res['idpost'];
        this.newPost['likes'] = 0;
        this.newPost['datetime'] = res['datetime'];
        delete this.newPost['token'];
        post = this.newPost;
        this.posts.push(post);
      }
      ,
      err => console.log(err)
    );
  }

  updateLikes ( post ) {
    const postid = {};
    postid['id'] = post['idpost'];
    postid['token'] = localStorage.getItem('token');
    console.log(postid);
    this._auth.incrLikes(postid).subscribe(
      res => {console.log(res);
        post['likes'] += 1;
      }
      ,
      err => console.log(err)
    );
  }

}
