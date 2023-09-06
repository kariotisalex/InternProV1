import {Component, OnInit} from '@angular/core';
import {Post} from "../../../post";
import {PostService} from "../../../post.service";
import {UserService} from "../../../user.service";
import {User} from "../../../user";
import {ActivatedRoute} from "@angular/router";
import {reportUnhandledError} from "rxjs/internal/util/reportUnhandledError";
import {map} from "rxjs";

@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrls: ['./post-details.component.css']
})
export class PostDetailsComponent implements OnInit{
  post! : Post;
  constructor(
    private postService : PostService,
    private userService : UserService,
    private route : ActivatedRoute
  ){}

  ngOnInit() {
    this.getPost();
  }
  get user() : User{
    return this.userService.getUser();
  }
  getPost() {
    const id = this.route.snapshot.paramMap.get('id')
    if(id){
      this.postService.getPostByPostid(this.user.uid, id)
        // .pipe(map(z =>{
        //   const y = z.filename;
        //   z.filename = `/api/post/${y}`
        //   console.log("show me " + z.pid);
        //   return z;
        // }))
        .subscribe({
          next: x => {
            console.log("show me " + x.pid);
            this.post = x;
          },error: err =>{
            console.log(err.error);
          }
        });
    }

  }

}
