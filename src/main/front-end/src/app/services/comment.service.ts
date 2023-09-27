import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import { Comment } from "./interfaces/comment";
import {Post} from "./interfaces/post";

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  comments! : Comment[];
  constructor(
    private http: HttpClient
  ) { }




  addNewComment(uid : string, pid : string, newComment : string) : Observable<any>{
    const body ={'comment': newComment}

    return this.http.post(`/api/user/${uid}/comment/${pid}`,body,{responseType: 'text'});
  }
  deleteComment(uid : string, cid : string) : Observable<any>{
    return this.http.delete(`/api/user/${uid}/comment/${cid}`,{responseType: 'text'});
  }
  updateComment(uid : string, cid : string, comment : string) : Observable<string>{
    const body = {'comment':comment}
    return this.http.put(`/api/user/${uid}/comment/${cid}`,body,{responseType: "text"});
  }
  countComments(pid : string) : Observable<number>{
    return this.http.get<number>(`/api/post/${pid}/comments/count`);
  }
  getPageCommentsByPostid(pid : string, startFrom : number, size : number) : Observable<Comment>{
    const params = new HttpParams()
      .set('startFrom', startFrom )
      .set('size',size);
    return this.http.get<Comment>(`/api/post/${pid}/comments/page`, {params: params});
  }

}
