import { Injectable } from '@angular/core';
import {HttpClient } from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  constructor(private http: HttpClient) { }

  getProfileImage(id: string): Observable<Blob> {
    return this.http.get('http://localhost:8080/linkedin/api/profile/picture', { responseType: 'blob' });
  }
}
