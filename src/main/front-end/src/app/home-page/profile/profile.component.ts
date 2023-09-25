import { Component, OnInit} from '@angular/core';
import { User } from "../../services/interfaces/user";
import { UserService } from "../../services/user.service";
import { ActivatedRoute, Route, Router } from "@angular/router";
import { Post } from "../../services/interfaces/post";
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
    this.countPosts();
    this.getFollowersCount();
    this.getFollowingCount();
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
          this.followers = x;
        }, error: (err : HttpErrorResponse) => {
          console.log(err.error)
          this.followers = [];
        }
      })
  }
  getFollowingUser(){
    this.followerService.getFollowingUser(this.user.uid)
      .subscribe({
        next: x => {
          this.followings = x;
        }, error: err => {
          this.followings = [];
        }
      })
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

  getFollowersCount(){
    this.followerService.getCountFollowers(this.user.uid)
      .subscribe({
        next: res=> {
          this.followersCount = res;
        },
        error: (err : HttpErrorResponse) =>{
          console.log(err.error);
          this.followersCount = 0;
        }

      });
  }

  getFollowingCount(){
    this.followerService.getCountFollowingUser(this.user.uid)
      .subscribe({
        next: res => {
          this.followingCount = res;
        },
        error: (err : HttpErrorResponse) =>{
          console.log(err.error);
          this.followingCount = 0;
        }

      });
  }

  followButton(){
    const loggedInUserid = this.userService.getUid();
    const guestUserid = this.user.uid;
    this.followerService.setFollow(loggedInUserid, guestUserid)
      .subscribe({
        next: x => {
          this.isRelation = true;
          this.getFollowersCount();
          this.getFollowingCount();
        },
        error: err => {
          this.isRelation = false;
          this.getFollowersCount();
          this.getFollowingCount();
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
          this.getFollowersCount();
          this.getFollowingCount();
        },
        error: err => {
          this.isRelation = true;
          this.getFollowersCount();
          this.getFollowingCount();
        }
      })
  }


  routing(){
    this.navigation.goToSettings();
  }

}

