import {Component, OnInit} from '@angular/core';
import {Post} from "../../../services/interfaces/post";
import {PostService} from "../../../services/post.service";
import {UserService} from "../../../services/user.service";
import {User} from "../../../services/interfaces/user";
import {ActivatedRoute} from "@angular/router";
import {reportUnhandledError} from "rxjs/internal/util/reportUnhandledError";
import {map} from "rxjs";
import {CommentService} from "../../../services/comment.service";
import {HttpErrorResponse} from "@angular/common/http";
import { Comment } from "../../../services/interfaces/comment";

@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrls: ['./post-details.component.css']
})
export class PostDetailsComponent implements OnInit{
  commentSubmission : String = "";
  commentSwitch : boolean = false;
  post! : Post;
  constructor(
    private postService : PostService,
    private userService : UserService,
    private route : ActivatedRoute,
    private commentService : CommentService
  ){}

  ngOnInit() {
    this.getPost();

    this.getComments();
  }


  get user() : User{
    return this.userService.getUser();
  }
  get comments() : Comment[] {
    return this.commentService.comments;
  }


  getPost() {
    const id = this.route.snapshot.paramMap.get('id')
    if(id){
      this.postService.getPostByPostid(this.user.uid, id)
        .pipe(map(z =>{
          const y = z.filename;
          z.filename = `/api/post/${y}`
          console.log("show me " + z.postid);
          return z;
        }))
        .subscribe({
          next: x => {
            this.post = x;
          },error: (err : HttpErrorResponse) =>{
            console.log(err.error);
          }
        });
    }
  }

  getComments(){
    const id = this.route.snapshot.paramMap.get('id')
    if(id){
      this.commentService.getAllCommentsByPostid(id)
        .subscribe({
          next: x => {
            this.commentSwitch = true;
            this.commentService.comments = x;
          },
          error: (err : HttpErrorResponse) => {

            console.log(err.error)
          }
        })
    }
  }

  onSubmit(){

  }
}
