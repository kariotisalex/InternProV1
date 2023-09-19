import { Component } from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {UserService} from "../services/user.service";
import {CommonModule, Location} from "@angular/common";
import {User} from "../services/interfaces/user";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {PostService} from "../services/post.service";
import {HttpErrorResponse} from "@angular/common/http";
import {NavigationService} from "../services/navigation.service";

@Component({
  selector: 'app-add-post',
    standalone: true,
  imports:[
    ReactiveFormsModule,
    CommonModule,
    RouterLink
  ],
  templateUrl: './add-post.component.html',
  styleUrls: ['./add-post.component.css']
})
export class AddPostComponent {
  error!          : string;
  succeed         : boolean = false;

  constructor(
    private router      : Router,
    private userService : UserService,
    private location    : Location,
    private postService : PostService,
    private navigation  : NavigationService
  ) {}


  get user(): User{
    return this.userService.getUser();
  }

  addPostHandling = new FormGroup({
    fileUpload : new FormControl(),
    description : new FormControl(),

  });

  onSubmitUpdate(event : any){
    const uid = this.userService.getUid();

    const file = event.currentTarget[0].files[0];
    const desc = this.addPostHandling.value.description as string;
    this.postService.addPost(uid, file, desc)
      .subscribe({
        next: x => {
          console.log('this is correct ' + x);
          this.navigation.goToMyProfile();
        },
        error: (e : HttpErrorResponse) => {
          console.log('This is error ' + e);
        }
      });


  }
  backButtonNav(){
    this.location.back();
  }

  succeededornot(){
    if(this.succeed){
      return 'greenClassFont'
    }else {
      return 'redClassFont'
    }
  }

}
