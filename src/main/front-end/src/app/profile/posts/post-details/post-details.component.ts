import {Component, OnInit} from '@angular/core';
import {Post} from "../../../services/interfaces/post";
import {PostService} from "../../../services/post.service";
import {UserService} from "../../../services/user.service";
import {User} from "../../../services/interfaces/user";
import {ActivatedRoute} from "@angular/router";
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
  private uid! : string;
  private cid! : string;
  valuees! : string;
  isAnyCommentExist : boolean = false;
  post! : Post;
  isUpdateCommentTabEnable : boolean = false;
  isUpdateDescTabEnable : boolean = false;

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
  backbuttonfunc(){
    if(!this.isNotificationInTextarea()){
      this.valuees = '';
    }
    this.isUpdateCommentTabEnable = false;
    this.isUpdateDescTabEnable = false;
  }

  get user() : User{
    return this.userService.getUser();
  }
  get comments() : Comment[] {
    return this.commentService.comments;
  }


  getPost() {
    const pid = this.route.snapshot.paramMap.get('id');
    if(pid){
      this.postService.getPostByPostid(this.user.uid, pid)
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
            this.isAnyCommentExist = true;
            this.commentService.comments = x;
          },
          error: (err : HttpErrorResponse) => {
            this.isAnyCommentExist=false;
            console.log(err.error)
          }
        })
    }
  }

  onSubmitNewComment(newComment : string){
    const uid = this.userService.getUid();
    const pid = this.route.snapshot.paramMap.get('id');
    if(pid){
      if ((newComment != '' && newComment)
      && (
          !this.isNotificationInTextarea()
        )){
        this.commentService.addNewComment(uid, pid, newComment)
          .subscribe({
            next: x => {
              this.valuees = "This comment posted successfully!";
              this.getComments();
            },
            error: err =>{
              this.valuees = "This comment failed to post!"
            }
          });
      }else {
        this.valuees = "Please write something to post!"
      }

    }
  }


  clearingNewCommentTextarea() {
    if (this.isNotificationInTextarea()) {
      this.valuees = "";
    }
  }

  isNotificationInTextarea(){
    if(
      (this.valuees == "This comment posted successfully!")
      || (this.valuees == "This comment failed to post!")
      || (this.valuees == "Please write something to post!")
      || (this.valuees == 'The comment updated!')
      || (this.valuees == 'The comment is not updated!')
      || (this.valuees == "The comment deleted!")
      || (this.valuees == "The comment is not deleted!")
      || (this.valuees == "The comment deleted!")
      || (this.valuees == "it doesn't changed!")
      || (this.valuees == "Post description changed!")
      || (this.valuees == "Post description does not changed!")
        ){
      return true;
    }else{
      return false;
    }
  }






  updateCommentCalling(cid : string, comment : string){
    this.isUpdateDescTabEnable = false;
    this.isUpdateCommentTabEnable = true;
    this.valuees = comment;
    this.cid = cid;
    this.uid = this.userService.getUid()
  }
  updateDescCalling(desc : string){
    this.isUpdateCommentTabEnable  = false;
    this.isUpdateDescTabEnable  = true;
    this.valuees = desc;
    this.uid = this.userService.getUid();

  }







  updateButtonFunc( comment : string){
    this.commentService.updateComment(this.uid, this.cid, comment)
      .subscribe({
        next: x => {
          this.valuees = 'The comment updated!'
          this.getComments();
          this.backbuttonfunc();
        },
        error: err => {
          this.valuees = 'The comment is not updated!'
        }
      })
  }
  deleteButtonFunc(){
    if (confirm("Are you sure?")){
      this.commentService.deleteComment(this.uid, this.cid)
        .subscribe({
          next: x => {
            this.valuees = "The comment deleted!"
            this.getComments();
            this.backbuttonfunc();
          },
          error: err => {
            this.valuees = "The comment is not deleted!"
          }
        })
    }
  }

  updateDescButtonFunc(){
    const pid = this.route.snapshot.paramMap.get('id');
    if(pid) {
      if(
           (this.valuees != '')
        && (!this.isNotificationInTextarea())
    ){
        this.postService.updateDesc(this.uid, pid, this.valuees)
          .subscribe({
            next: x => {
              this.valuees = "Post description changed!"
              this.getPost();
            },
            error: err => {
              this.valuees = "Post description does not changed!"
            }
          })
      }

    }

  }
}
