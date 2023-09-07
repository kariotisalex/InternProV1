import { Component } from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {UserService} from "../services/user.service";
import {CommonModule, Location} from "@angular/common";
import {User} from "../services/interfaces/user";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {PostService} from "../services/post.service";

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
  error!          : String;
  succeed         : boolean = false;

  constructor(
    private router      : Router,
    private userService : UserService,
    private location    : Location,
    private postService : PostService,
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
    this.postService.addPost(uid, file, desc);


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
