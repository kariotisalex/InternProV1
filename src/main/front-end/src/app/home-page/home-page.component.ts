import { Component } from '@angular/core';
import {UserService} from "../user.service";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent {

  get userID(): string{
    return this.userService.user.uid;
  }
  get usernameU(): string{
    return this.userService.user.username;
  }

  constructor(
    private userService : UserService
  ) {}




}
