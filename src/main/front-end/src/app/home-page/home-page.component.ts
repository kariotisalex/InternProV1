import { Component } from '@angular/core';
import { UserService } from "../user.service";
import { User } from "../user";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent {

  constructor(
    private userService : UserService
  ) {}

  get user(): User{
    return this.userService.getUser();
  }

}
