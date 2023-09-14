import { Component } from '@angular/core';
import { FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { FormControl } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { CommonModule } from "@angular/common";
import { HttpErrorResponse } from "@angular/common/http";
import { UserService } from "../services/user.service";
import {NavigationService} from "../services/navigation.service";


@Component({
  selector: 'app-login',
  standalone: true,
    imports: [
        ReactiveFormsModule,
        CommonModule,
        RouterLink
    ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  error : string = "";

  constructor(
    private router : Router,
    private userService:UserService,
    private navigation : NavigationService
  ) {}

  loginHandling = new FormGroup({
    username : new FormControl('',[Validators.required]),
    password : new FormControl('', [Validators.required])
  });

  onSubmit(){
    const username : string = this.loginHandling.value.username as string;
    const password : string = this.loginHandling.value.password as string;
    this.userService.logIn(username,password)
    .subscribe( {
      next: x => {
        this.userService.loggedinUser(x);
        this.navigation.goToHome();
      },
      error: (err: HttpErrorResponse) => {
        this.error = err.error;
      }
    });
  }

  changing(){
    this.error="";
  }
}

