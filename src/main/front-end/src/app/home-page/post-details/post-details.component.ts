import {Component, OnInit} from '@angular/core';
import {Post} from "../../services/interfaces/post";
import {PostService} from "../../services/post.service";
import {UserService} from "../../services/user.service";
import {User} from "../../services/interfaces/user";
import {ActivatedRoute, Router} from "@angular/router";
import {catchError, map} from "rxjs";
import {CommentService} from "../../services/comment.service";
import {HttpErrorResponse} from "@angular/common/http";
import { Comment } from "../../services/interfaces/comment";
import {NavigationService} from "../../services/navigation.service";


@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrls: ['./post-details.component.css']
})
export class PostDetailsComponent implements OnInit{
  private uid! : string;
  private cid! : string;
  isUpdateCommentTabEnable : boolean = false;
  isUpdateDescTabEnable : boolean = false;
  valuees! : string;
  isAnyCommentExist : boolean = false;
  post! : Post;
  commentsPerPage : number = 10;
  commentPages : number[] = [];

  constructor(
    private postService    : PostService,
    private userService    : UserService,
    private commentService : CommentService,
    private route          : ActivatedRoute,
    private router         : Router,
    private navigation     : NavigationService
  ){}

  ngOnInit() {
    this.getPost();
    this.getComments(1);
    this.countComments();
  }
  get user() : User{
    return this.userService.getUser();
  }
  get comments() : Comment[] {
    return this.commentService.comments;
  }

  getPost() {
    this.route.paramMap
      .subscribe({
        next: res => {
          const pid = res.get('id');
          if(pid){
            this.postService.getPostByPostid(this.user.uid, pid)
              .pipe(map(post =>{
                post.filename = `/api/post/${post.filename}`
                console.log("show me " + post.postid);
                return post;
              }))
              .subscribe({
                next: res => {
                  this.post = res;
                },error: (err : HttpErrorResponse) =>{
                  console.log(err.error);
                }
              });
          }
        }
      });
  }

  countComments(){
    this.commentPages = [];
    this.route.paramMap
      .subscribe({
        next: prmMap => {
          const pid = prmMap.get('id')
          if (pid){
            this.commentService.countComments(pid)
              .subscribe({
                next: amountOfCount => {
                  console.log('xxx' + amountOfCount);
                      if (amountOfCount % this.commentsPerPage != 0) {
                        for (let i = 1; i <= (amountOfCount / this.commentsPerPage) + 1; i++) {
                          this.commentPages.push(i);
                        }
                      } else {
                        for (let i = 1; i <= (amountOfCount / this.commentsPerPage); i++) {
                          this.commentPages.push(i);
                        }
                      }
                }
              })
          };
        }
      });
  }

  getComments(page : number) {
    let startFrom: number = (page - 1) * this.commentsPerPage;

    this.route.paramMap
      .subscribe({
        next: prmMap => {
          const pid = prmMap.get('id')
          if (pid) {
            this.commentService.getPageCommentsByPostid(
              pid,
              startFrom,
              this.commentsPerPage)
              .subscribe({
                next: listOfComments => {
                  this.isAnyCommentExist = true;
                  this.commentService.comments = listOfComments;
                },
                error: (err: HttpErrorResponse) => {
                  this.isAnyCommentExist = false;
                  this.commentPages = [0];

                }
              });
          }
        }
      });
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

  clearingNewCommentTextarea() {
    if (this.isNotificationInTextarea()) {
      this.valuees = "";
    }
  }





  onSubmitNewComment(newComment : string) {
    const uid = this.userService.getUid();

    this.route.paramMap
      .subscribe({
        next: prmMap => {
          const pid = prmMap.get('id')
          if (pid) {
            if ((newComment != '' && newComment)
              && (
                !this.isNotificationInTextarea()
              )) {
              this.commentService.addNewComment(uid, pid, newComment)
                .subscribe({
                  next: x => {
                    this.valuees = "This comment posted successfully!";
                    this.getComments(1);
                    this.countComments();
                  },
                  error: err => {
                    this.valuees = "This comment failed to post!"
                  }
                });
            } else {
              this.valuees = "Please write something to post!"
            }
          }
        }
      });
  }


// callings
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
  deletePostCalling(){
    this.isUpdateCommentTabEnable  = true;
    this.isUpdateDescTabEnable  = true;
    this.uid = this.userService.getUid();

  }





  // Buttons functions
  updateButtonFunc( comment : string){
    this.commentService.updateComment(this.uid, this.cid, comment)
      .subscribe({
        next: x => {
          this.valuees = 'The comment updated!'
          this.getComments(1);
          this.countComments();
          this.backbuttonfunc();
        },
        error: err => {
          this.valuees = 'The comment is not updated!'
        }
      })
  }
  deleteCommentButtonFunc(){
    if (confirm("Are you sure?")){
      this.commentService.deleteComment(this.uid, this.cid)
        .subscribe({
          next: x => {
            this.valuees = "The comment deleted!"
            this.getComments(1);
            this.countComments();
            this.backbuttonfunc();
          },
          error: err => {
            this.valuees = "The comment is not deleted!"
          }
        })
    }
  }
  updateDescButtonFunc() {
    this.route.paramMap
      .subscribe({
        next: prmMap => {
          const pid = prmMap.get('id')
          if (pid) {
            if ((this.valuees != '') && (!this.isNotificationInTextarea())) {
              this.postService.updateDesc(this.uid, pid, this.valuees)
                .subscribe({
                  next: x => {
                    this.valuees = "Post description changed!"
                    this.getPost();
                  },
                  error: err => {
                    this.valuees = "Post description does not changed!"
                  }
                });
            }
          }
        }
      });
  }

  deletePostButtonFunc() {
    this.route.paramMap
      .subscribe({
        next: prmMap => {
          const pid = prmMap.get('id')
          if (pid) {
            this.postService.deletePost(this.uid, pid)
              .subscribe({
                next: x => {
                  this.postService.posts = [];
                  this.postsNav();
                },
                error: err => {
                  console.log(err.error)
                }
              });
          }
        }
      })
  }

  backbuttonfunc(){
    if(!this.isNotificationInTextarea()){
      this.valuees = '';
    }
    this.isUpdateCommentTabEnable = false;
    this.isUpdateDescTabEnable = false;
  }

  postsNav(){
    this.navigation.goToPosts();
  }
}
