import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Post} from "./interfaces/post";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})



export class PostService {
  posts! : Post[] ;
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
        error: (e : HttpErrorResponse) => {
          console.log('This is error ' + e);
        }
      });
  }

  getPostsById(uid : string) : Observable<Post[]>{
    return this.http.get<Post[]>(`/api/user/${uid}/posts`);
  }

  getPostByPostid(uid : String, postid : String) : Observable<Post> {
    return this.http.get<Post>(`/api/user/${uid}/post/${postid}`);
  }
}
