import { Component } from '@angular/core';
import { FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { FormControl } from "@angular/forms";
import { AlexgramService } from "../alexgram.service";


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  constructor(
    private alexgramService : AlexgramService
  ) {}

  loginHandling = new FormGroup({
    username : new FormControl('',[Validators.required ]),
    password : new FormControl('', Validators.required),
  });



  onSubmit(){
    const username : String = this.loginHandling.value.username as String;
    const password : String = this.loginHandling.value.password as String;
    const result = this.alexgramService.isLoggedIn(username,password);
    if(result){
      result
        .subscribe(v => {
          console.log(v)
          },error => {console.log("wrong")})
    }else{
      console.log("Gone wrong!");

    }
  }

}

