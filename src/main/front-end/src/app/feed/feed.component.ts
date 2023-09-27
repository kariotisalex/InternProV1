import {Component, OnInit} from '@angular/core';
import {Post, Posts} from "../services/interfaces/post";
import {PostService} from "../services/post.service";
import {User} from "../services/interfaces/user";
import {UserService} from "../services/user.service";
import {CommentService} from "../services/comment.service";
import {ActivatedRoute} from "@angular/router";
import {NavigationService} from "../services/navigation.service";
import {map} from "rxjs";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent implements OnInit{
  pages : number[] = [];
  posts : Posts[] = [];
  postsPerPage : number = 6;
  commentsPerPage : number = 10;
  post! : Posts;


  constructor(
    private postService : PostService,
    private userService : UserService,
    private commentService : CommentService,
    private route          : ActivatedRoute,
    private navigation     : NavigationService
  ) {}

  ngOnInit(): void {
    this.getFeed(1);

  }

  getFeed(page : number){

    let startFrom : number = (page - 1) * this.postsPerPage;

    this.postService.getFeed(this.userService.getUid(), startFrom,this.postsPerPage)
      .pipe(map(x => {x.posts.map(z =>{
        if(z.userid){
          z.filename = `/api/post/${z.filename}`
        }
        return z;
      })
      return x;
      }))
      .subscribe({
        next: x => {
          this.posts = [];
          this.pages = [];
          const head = x.count;

          if (head % this.postsPerPage != 0) {
            for (let i = 1; i <= (head / this.postsPerPage) + 1; i++) {
              this.pages.push(i);
            }
          } else {
            for (let i = 1; i <= (head / this.postsPerPage); i++) {
              this.pages.push(i);
            }
          }
          this.posts = x.posts;
          console.log(x)

          console.log("afdadfas")
        },error: (err : HttpErrorResponse) => {
          console.log(err.error)
          this.posts = [];
          this.pages = [];
        }
      })


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
              .pipe(map(post =>{
                post.filename = `/api/post/${post.filename}`
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

  deletePostButtonFunc() {
    if (this.post.userid == this.user.uid) {
      if (confirm("Do you want to delete this post?")) {
        this.postService.deletePost(this.user.uid, this.post.postid)
          .subscribe({
            next: x => {
              this.postService.posts = [];
              this.navigation.goToProfile();
            },
            error: err => {
              console.log(err.error)
            }
          });
      }
    }


  }}
