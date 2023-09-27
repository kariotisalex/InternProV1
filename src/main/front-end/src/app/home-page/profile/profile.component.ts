import { Component, OnInit} from '@angular/core';
import { User } from "../../services/interfaces/user";
import { UserService } from "../../services/user.service";
import { ActivatedRoute, Route, Router } from "@angular/router";
import {Post, Posts} from "../../services/interfaces/post";
import { PostService } from "../../services/post.service";
import { map } from "rxjs";
import { HttpErrorResponse } from "@angular/common/http";
import { NavigationService } from "../../services/navigation.service";
import { FollowerService } from "../../services/follower.service";
import { Follower } from "../../services/interfaces/follower";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit{
  isMyProfile : boolean = true;
  isRelation : boolean = false;
  followersCount : number = 0;
  followingCount : number = 0;
  postsPerPage : number = 6;
  pages : number[] = [];
  user! : User;
  profileShow : boolean = true;
  followersPointer! : boolean;
  followers : Follower[] = [];
  followings : Follower[] = [];


  constructor(
    private userService : UserService,
    private router : Router,
    private postService : PostService,
    private followerService : FollowerService,
    private navigation : NavigationService,
    private route : ActivatedRoute
  ) {}

  ngOnInit(){
    this.getFunc();
    this.getPosts(1);
    this.getFollowers();
    this.getFollowingUser();


  }
  profileShowFunc(status :  boolean, pointer : boolean){
    this.followersPointer = pointer;
    this.profileShow = status;
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
            this.getFollowers();
            this.getFollowingUser();
            this.isMyProfile = false;
            this.getIsRelation(this.userService.getUid(), this.user.uid);
          } else {
            console.log(this.userService.getUid());
            this.user = this.userService.getUser();
          }
        }
      });
  }

  getFollowers(){
    this.followerService.getFollowers(this.user.uid)
      .subscribe({
        next: x => {
          console.log(x)
          this.followers = [];
          for(let follower of x){
            if(follower.followid){
              this.followers.push(follower);
            }else {
              console.log(follower)
              this.followersCount = follower.usernameUserid as unknown as number;


            }
          }
        }, error: (err : HttpErrorResponse) => {
          console.log(err.error)
          this.followers = [];
          this.followersCount = 0;
        }
      })
  }







  getFollowingUser(){
    this.followerService.getFollowingUser(this.user.uid)
      .subscribe({
        next: x => {
          this.followings = [];
          for(let followingUser of x){
            if(followingUser.followid){

              this.followings.push(followingUser);

            }else {
              this.followingCount = followingUser.usernameUserid as unknown as number;


            }
          }


        }, error: err => {
          this.followings = [];
          this.followingCount = 0;
        }
      })
  }





  get posts() : Posts[]{
    return this.postService.posts;
  }

  getPosts(page : number){
    let startFrom : number = (page - 1) * this.postsPerPage;
    this.postService.posts = [];
    this.pages =[];

    this.postService.getPostsByUserid(this.user.uid,
      startFrom ,
      this.postsPerPage )
      .pipe(map(x => x.map(z =>{
        if(z.userid){
          z.filename = `/api/post/${z.filename}`
        }
        return z;
      })))
      .subscribe({
        next: x => {
          this.postService.posts = [];
          this.pages = [];
          const head = x[0].username as unknown as number;

          if (head % this.postsPerPage != 0) {
            for (let i = 1; i <= (head / this.postsPerPage) + 1; i++) {
              this.pages.push(i);
            }
          } else {
            for (let i = 1; i <= (head / this.postsPerPage); i++) {
              this.pages.push(i);
            }
          }
          for (let i = 1; i < x.length; i++) {
            this.postService.posts.push(x[i])
          }
          console.log(x)
        },
        error: (err : HttpErrorResponse) => {

          console.log(err.error);
        }
      });
  }

  postNav (pst : string) : void {
    this.navigation.goToPostDetail(pst)
  }
  getIsRelation(loggedInUserid : string, guestUserid : string){
    this.followerService.getIsRelation(loggedInUserid, guestUserid)
      .subscribe({
        next: x => {
          this.isRelation = true;
        }, error: (err:HttpErrorResponse) => {
          console.log(err.error)
          this.isRelation = false;
        }
      })
  }



  followButton(){
    const loggedInUserid = this.userService.getUid();
    const guestUserid = this.user.uid;
    this.followerService.setFollow(loggedInUserid, guestUserid)
      .subscribe({
        next: x => {
          this.isRelation = true;
          this.getFollowers();
          this.getFollowingUser();
        },
        error: err => {
          this.isRelation = false;
          this.getFollowers();
          this.getFollowingUser();
        }
      })

  }

  unfollowButton(){
    const loggedInUserid = this.userService.getUid();
    const guestUserid = this.user.uid;
    this.followerService.setUnfollow(loggedInUserid , guestUserid )
      .subscribe({
        next: x => {
          this.isRelation = false;
          this.getFollowers();
          this.getFollowingUser();
        },
        error: err => {
          this.isRelation = true;
          this.getFollowers();
          this.getFollowingUser();
        }
      })
  }


  routing(){
    this.navigation.goToSettings();
  }

}

