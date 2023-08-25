import { Component } from '@angular/core';
import { UserService } from "../user.service";
import { User } from "../user";
import {Router} from "@angular/router";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent {

  private emptyUser: User={
    uid: '',
    username:''
  }
  constructor(
    private userService : UserService,
    private router : Router
  ) {}

  get user(): User{
    return this.userService.getUser();
  }

  get logout(){
     this.userService.logout();
     this.router.navigateByUrl("/login")
    return true;
  }

}
