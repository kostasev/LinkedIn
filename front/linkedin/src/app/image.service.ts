import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  constructor(private http: HttpClient) { }

  getProfileImage(id: string): Observable<Blob> {
    return this.http.get('http://localhost:8080/linkedin/api/profile/picture/' + id , { responseType: 'blob'});
  }

  onUpload(fd) {
    return this.http.post('http://localhost:8080/linkedin/api/profile/upload', fd );
  }
}
