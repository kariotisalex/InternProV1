import {Component, OnInit} from '@angular/core';
import {User} from "../../services/interfaces/user";
import {UserService} from "../../services/user.service";
import {ActivatedRoute, Route, Router} from "@angular/router";
import {Post} from "../../services/interfaces/post";
import {PostService} from "../../services/post.service";
import {map} from "rxjs";
import {HttpErrorResponse} from "@angular/common/http";
import {NavigationService} from "../../services/navigation.service";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit{

  postsPerPage : number = 6;
  pages : number[] = [];
  user! : User;


  constructor(
    private userService : UserService,
    private router : Router,
    private postService : PostService,
    private navigation : NavigationService,
    private route : ActivatedRoute
  ) {}

  ngOnInit(){
    this.getFunc();
    this.getPosts(1);
    this.countPosts();

  }
  getFunc(){
    this.route.paramMap
      .subscribe({
        next: res => {
          const uid = res.get('id');
          const username = res.get('username')
          if (uid && username) {
            this.user = {
              uid: uid,
              username: username
            }
          } else {
            console.log(this.userService.getUid())
            this.user = this.userService.getUser()
          }
        }
      });
  }



  get posts() : Post[]{
    return this.postService.posts;
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
    this.postService.posts = [];

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

  postNav (pst : string) : void {
    this.navigation.goToPostDetail(pst)
  }





  routing(){
    this.navigation.goToSettings();
  }

}

