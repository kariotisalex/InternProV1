import { Component, Input, OnInit } from '@angular/core';
import { Post } from "../../services/interfaces/post";
import { PostService } from "../../services/post.service";
import { UserService } from "../../services/user.service";
import { User } from "../../services/interfaces/user";
import { map } from "rxjs";
import { Router } from "@angular/router";
import { HttpErrorResponse } from "@angular/common/http";

@Component({
  selector: 'app-posts',

  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css']
})
export class PostsComponent implements OnInit{
  postsPerPage : number = 6;
  pages : number[] = [];

  constructor(
    private postService : PostService,
    private userService : UserService,
    private router      : Router
  ) {}


  get user() : User {
    return this.userService.getUser();
  }

  get posts() : Post[]{
    return this.postService.posts;
  }

  ngOnInit(){
    this.getPosts(1);
    this.countPosts();

  }

  countPosts(){
    this.postService.countPosts(this.user.uid)
      .subscribe({
        next: x => {
          if (x % this.postsPerPage != 0){
            for (let i = 1; i <= (x / this.postsPerPage) + 1; i++){
              this.pages.push(i);
            }
          }else {
            for (let i = 1; i <= (x / this.postsPerPage); i++){
              this.pages.push(i);
            }
          }

        },error: err => {

        }
      })
  }

  getPosts(page : number){
    let startFrom : number = (page - 1) * this.postsPerPage;

    this.postService.getPostsByUserid(this.user.uid,
      startFrom ,
      this.postsPerPage )
      .pipe(map(x => x.map(z =>{
        z.filename = `/api/post/${z.filename}`
        return z;
      })))
      .subscribe({
        next: x => {

          this.postService.posts = x ;
        },
        error: (err : HttpErrorResponse) => {
          console.log(err.error);
        }
      });
  }
  postNav(pst : string){
    this.router.navigateByUrl(`/home/post/${pst}`);
  }


}
