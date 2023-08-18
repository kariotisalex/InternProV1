import { Component } from '@angular/core';
import { FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { FormControl } from "@angular/forms";
import { AlexgramService } from "../alexgram.service";
import { Router} from "@angular/router";
import { CommonModule} from "@angular/common";
import {HttpErrorResponse} from "@angular/common/http";


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  error : String = "";

  constructor(
    private alexgramService : AlexgramService,
    private route : Router
  ) {}

  loginHandling = new FormGroup({
    username : new FormControl('',[Validators.required ]),
    password : new FormControl('', Validators.required),
  });



  onSubmit(){
    const username : String = this.loginHandling.value.username as String;
    const password : String = this.loginHandling.value.password as String;
    const result = this.alexgramService.isLoggedIn(username,password);
    result.subscribe( x => {
        console.log(x.uid);
        this.route.navigateByUrl("/testing");
      },(err: HttpErrorResponse) => {
        this.error=err.error;
      });
  }
  changing(){
    this.error="";
  }
}

