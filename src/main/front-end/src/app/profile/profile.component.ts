import {Component, Input} from '@angular/core';
import {User} from "../user";
import {UserService} from "../user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent {

  constructor(
    private userService : UserService,
    private router : Router
  ) {
  }
  get user() : User {
    return this.userService.getUser();
  }
  routing(){
    this.router.navigateByUrl('/home/profile/settings');
  }

}

