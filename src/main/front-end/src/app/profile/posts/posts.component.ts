import {Component, Input, OnInit} from '@angular/core';
import {Post} from "../../post";
import {PostService} from "../../post.service";
import {UserService} from "../../user.service";
import {User} from "../../user";
import {map} from "rxjs";

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css']
})
export class PostsComponent implements OnInit{
  posts! : Post[] ;
  constructor(
    private postService : PostService,
    private userService : UserService
  ) {}

  get user() : User {
    return this.userService.getUser();
  }

  ngOnInit(){
     this.postService.getPostsById(this.user.uid)
       .pipe(map(x => x.map(z =>{
         const y = z.filename;
         z.filename = `/api/post/${y}`
          return z;
       })))
       .subscribe({
         next: x => {
           console.log(x);
           this.posts = x;
         },
         error: err => {
           console.log(err.error);
         }
       });
  }



}
