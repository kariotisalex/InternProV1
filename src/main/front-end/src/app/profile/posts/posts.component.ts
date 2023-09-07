import {Component, Input, OnInit} from '@angular/core';
import {Post} from "../../services/interfaces/post";
import {PostService} from "../../services/post.service";
import {UserService} from "../../services/user.service";
import {User} from "../../services/interfaces/user";
import {map} from "rxjs";
import {Router, RouterModule} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-posts',

  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css']
})
export class PostsComponent implements OnInit{

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
     this.postService.getPostsById(this.user.uid)
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
    this.router.navigateByUrl(`/home/profile/post/${pst}`);
  }


}
