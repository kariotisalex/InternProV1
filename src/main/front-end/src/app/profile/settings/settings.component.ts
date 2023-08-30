import { Component } from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {UserService} from "../../user.service";
import {User} from "../../user";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import { Location } from "@angular/common";


@Component({
  selector: 'app-settings',
  standalone: true,
  imports:[
    ReactiveFormsModule,
    CommonModule,
    RouterLink
  ],

  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent{

  error! : String;

  constructor(
    private router : Router,
    private userService : UserService,
    private location : Location
  ) {}

  get user(): User{
    return this.userService.getUser();
  }

  updateHandling = new FormGroup({
    currentPassword : new FormControl(),
    newPassword : new FormControl()
  });
  onSubmit(){

  }

  changing(){
    this.error="";
  }
  backButtonNav(){
    this.location.back();
  }

}

