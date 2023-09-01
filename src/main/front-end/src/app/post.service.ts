import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(
    private http : HttpClient
  ) { }

  addPost(uid : String, data : FormData){
    this.http.post(`/api/user/${uid}/post`,data)
      .subscribe({
        next: x => {
          console.log('this is correct ' + x);
        },
        error: e => {
          console.log('This is error ' + e);
        }
      });
  }
}
