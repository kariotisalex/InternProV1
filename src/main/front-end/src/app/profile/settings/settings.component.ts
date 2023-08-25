import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {UserService} from "../../user.service";
import {User} from "../../user";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";


@Component({
  selector: 'app-settings',
  standalone: true,
  imports:[
    ReactiveFormsModule
  ],

  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent{

  error! : String;

  constructor(
    private router : Router,
    private userService : UserService
  ) {}

  get user(): User{
    return this.userService.getUser();
  }

  updateHandling = new FormGroup({
    currentPassword : new FormControl(),
    newPassword : new FormControl()
  });
}

