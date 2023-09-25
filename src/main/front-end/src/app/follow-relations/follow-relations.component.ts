import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Router} from "@angular/router";
import {UserService} from "../services/user.service";
import {User} from "../services/interfaces/user";
import {Follower} from "../services/interfaces/follower";
import {FollowerService} from "../services/follower.service";
import {HttpErrorResponse} from "@angular/common/http";
import {NavigationService} from "../services/navigation.service";


@Component({
  selector: 'app-follow-relations',
  templateUrl: './follow-relations.component.html',
  styleUrls: ['./follow-relations.component.css']
})
export class FollowRelationsComponent implements OnInit{

  @Input () followersPointer : boolean = true;
  @Output() data = new EventEmitter();
  @Input() followers! : Follower[];
  @Input ()followings! : Follower[];

  constructor(
    private router : Router,
    private userService : UserService,
    private followerService : FollowerService,
    public navigation : NavigationService

  ) {}

  ngOnInit() {

  }

  get user(): User{
    return this.userService.getUser();
  }



  backButtonNav(){
    this.data.emit();
  }
  updater(){
    this.followersPointer = true;
  }
  deleter(){
    this.followersPointer = false;
  }

}
