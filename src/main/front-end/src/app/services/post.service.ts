import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {Post, Posts} from "./interfaces/post";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})



export class PostService {
  numberOfPosts! : number;
  posts! : Posts[] ;
  constructor(
    private http : HttpClient
  ) { }

  addPost(uid : string, file : File, desc: string){
    const formObj = new FormData();
    formObj.append('file', file);
    formObj.append('desc', desc);
    return this.http.post(`/api/user/${uid}/post`,formObj, {responseType: "text"});
  }

  getPostsById(uid : string) : Observable<Post[]>{
    return this.http.get<Post[]>(`/api/user/${uid}/posts`);
  }

  getPostByPostid(uid : string, postid : string) : Observable<Posts> {
    return this.http.get<Posts>(`/api/user/${uid}/post/${postid}`);
  }

  updateDesc(uid : string, pid : string, desc : string) : Observable<string>{
    const body = {
      'desc':desc
    }
    return this.http.put(`/api/user/${uid}/post/${pid}`,body,{responseType: 'text'})
  }

  deletePost(uid : string, pid : string){
    return this.http.delete(`/api/user/${uid}/post/${pid}`)
  }

  countPosts(uid : string) : Observable<number>{
    return this.http.get<number>(`/api/user/${uid}/posts/count`);
  }

  getPostsByUserid(uid : string, startFrom : number, size : number) : Observable<Posts[]>{
    const params = new HttpParams()
      .set('startFrom', startFrom )
      .set('size',size );
    return this.http.get<Posts[]>(`/api/user/${uid}/posts/page`, {params: params});
  }

  getFeed(uid : string, startFrom : number, size : number) : Observable<Post>{
    const params = new HttpParams()
      .set('startFrom', startFrom )
      .set('size',size );
    return this.http.get<Post>(`/api/user/${uid}/feed`, {params : params})
  }


}
