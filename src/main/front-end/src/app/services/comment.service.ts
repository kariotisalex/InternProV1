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
}
