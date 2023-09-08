import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import { Comment } from "./interfaces/comment";

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  comments! : Comment[];
  constructor(
    private http: HttpClient
  ) { }

  getAllCommentsByPostid(postid : string) : Observable<Comment[]>{
    return this.http.get<Comment[]>(`/api/post/${postid}/comments`);
  }

  addNewComment(uid : String, pid : String, newComment : string) : Observable<any>{
    const body ={'comment': newComment}

    return this.http.post(`/api/user/${uid}/comment/${pid}`,body,{responseType: 'text'});
  }
  deleteComment(uid : String, cid : String) : Observable<any>{
    return this.http.delete(`/api/user/${uid}/comment/${cid}`,{responseType: 'text'});
  }
  updateComment(uid : string, cid : string, comment : string) : Observable<String>{
    const body = {'comment':comment}
    return this.http.put(`/api/user/${uid}/comment/${cid}`,body,{responseType: "text"});
  }

}
