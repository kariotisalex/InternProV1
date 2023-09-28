import {Component, OnInit} from '@angular/core';
import {Post, Posts} from "../../services/interfaces/post";
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
  commentsPerPage : number = 10;
  post! : Posts;


  constructor(
    private postService    : PostService,
    private userService    : UserService,
    private commentService : CommentService,
    private route          : ActivatedRoute,
    private navigation     : NavigationService
  ){}

  ngOnInit() {
    this.getPost();
  }

  get user() : User{
    return this.userService.getUser();
  }

  getPost() {
  this.route.paramMap
    .subscribe({
      next: res => {
        const pid = res.get('id');
        if (pid) {
            this.postService.getPostByPostid(this.user.uid, pid)
              .pipe(map(posts =>{
                posts.filename = `/api/post/${posts.filename}`
                console.log("show me " + posts.postid);
                return posts;
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

  deletePostButtonFunc() {
    if(this.post.userid == this.user.uid){
      if (confirm("Do you want to delete this post?")){
        this.postService.deletePost(this.user.uid, this.post.postid)
          .subscribe({
            next: x => {
              this.postService.posts = [];
              this.navigation.goToProfileView(this.userService.getUid(), this.userService.getUsername());
            },
            error: err => {
              console.log(err.error)
            }
          });
      }
    }


  }
}
