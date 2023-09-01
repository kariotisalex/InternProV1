import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(
    private http : HttpClient
  ) { }

  addPost(uid : String, file : File, desc: string){
    const formObj = new FormData();
    formObj.append('file', file);
    formObj.append('desc', desc);
    this.http.post(`/api/user/${uid}/post`,formObj)
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
