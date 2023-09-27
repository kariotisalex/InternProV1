import {Component, OnInit} from '@angular/core';
import { UserService } from "../services/user.service";
import { User } from "../services/interfaces/user";
import {Router} from "@angular/router";
import {NavigationService} from "../services/navigation.service";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit{


  constructor(
    private userService : UserService,
    private router : Router,
    public navigation : NavigationService
  ) {}

  ngOnInit(): void {
    this.userService.getUsername();
  }

  get user(): User{
    return this.userService.getUser();
  }

  get logout(){
     this.userService.logout();
     this.navigation.goToLogin();
    return true;
  }


}
