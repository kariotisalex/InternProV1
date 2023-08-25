import { Component } from '@angular/core';
import { FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { FormControl } from "@angular/forms";
import { AlexgramService } from "../alexgram.service";
import {Router, RouterLink} from "@angular/router";
import { CommonModule} from "@angular/common";
import {HttpErrorResponse} from "@angular/common/http";
import { HomePageComponent } from "../home-page/home-page.component";
import {UserService} from "../user.service";


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

  error : String = "";

  constructor(
    private router : Router,
    private userService:UserService
  ) {}

  loginHandling = new FormGroup({
    username : new FormControl('',[Validators.required]),
    password : new FormControl('', [Validators.required])
  });

  onSubmit(){
    const username : String = this.loginHandling.value.username as String;
    const password : String = this.loginHandling.value.password as String;
    this.userService.logIn(username,password)
    .subscribe( {
      next: x => {
        this.userService.loggedinUser(x);
        this.router.navigateByUrl("/home");
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

