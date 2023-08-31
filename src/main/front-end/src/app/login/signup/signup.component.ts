import { Component } from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {HttpErrorResponse} from "@angular/common/http";
import {Router, RouterLink} from "@angular/router";
import {UserService} from "../../user.service";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    RouterLink
  ],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {

  error : String = "";
  value : String = "";
  valueV: String = "";
  constructor(
    private router : Router,
    private userService : UserService
  ) {}


  signupHandling = new FormGroup({
    username : new FormControl('',[Validators.required ]),
    password : new FormControl('', Validators.required),
    passwordV : new FormControl('', Validators.required),
  });

  onSubmit(){
    const username  : String = this.signupHandling.value.username as String;
    const password  : String = this.signupHandling.value.password as String;
    const passwordV : String = this.signupHandling.value.passwordV as String;

    if (passwordV == password){
      this.userService.signup(username,password)
        .subscribe( {
          next: x => {
            this.router.navigateByUrl("/login");
          },
          error: (err: HttpErrorResponse) => {
            this.error = err.error;
          }
        });
    }else{
      this.error = "Password is not the same in fields!"
      this.value = "";
      this.valueV = "";
    }


  }

  changing(){
    this.error="";
  }
  checking() : String {
    const password  : String = this.signupHandling.value.password as String;
    const passwordV : String = this.signupHandling.value.passwordV as String;
    if ((password != "") && (passwordV != "")){
      if(passwordV == password){
        return 'greenClass'
      }else{
        return 'redClass'
      }
    }else {
      return '';
    }
  }
}
