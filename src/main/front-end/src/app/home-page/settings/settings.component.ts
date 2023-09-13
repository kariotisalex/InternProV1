import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {UserService} from "../../services/user.service";
import {User} from "../../services/interfaces/user";
import {FormControl, FormGroup, ReactiveFormsModule, Validator, Validators} from "@angular/forms";
import {CommonModule} from "@angular/common";
import { Location } from "@angular/common";
import {HttpErrorResponse} from "@angular/common/http";
import {NavigationService} from "../../services/navigation.service";


@Component({
  selector: 'app-settings',
  standalone: true,
  imports:[
    ReactiveFormsModule,
    CommonModule,
    RouterLink
  ],

  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent {

  error!          : String;
  update          : boolean = true;
  currentPswd     : String = "";
  newPswd         : String = "";
  confirmNewPswd  : String = "";
  delPswd         : String = "";
  succeed         : boolean = false;

  constructor(
    private router : Router,
    private userService : UserService,
    private location : Location,
    private navigation : NavigationService
  ) {}


  get user(): User{
    return this.userService.getUser();
  }

  updateHandling = new FormGroup({
    currentPassword : new FormControl('',[Validators.required]),
    newPassword : new FormControl('',[Validators.required]),
    confirmNewPassword : new FormControl('',[Validators.required])
  });

  onSubmitUpdate(){
    const uid = this.userService.getUid();
    const currentPassword = this.updateHandling.value.currentPassword as String;
    const newPassword = this.updateHandling.value.newPassword as String;
    const confirmNewPassword = this.updateHandling.value.confirmNewPassword as String;
    if ((newPassword == confirmNewPassword) && (currentPassword != newPassword)){
      this.userService.changePassword(uid, currentPassword, newPassword)
        .subscribe({
          next: x => {
            this.succeed = true;
            this.error = "Password changed!";
          },
          error: (e : HttpErrorResponse) => {
            this.succeed = false;

            this.error = "Password not changed";
          }
        })
    }

  }
  delete(){
    const uid = this.userService.getUid();
    console.log(uid);
    this.userService.delete(uid)
      .subscribe({
        next: x =>{
          this.succeed = true;
          this.error = "Deleted successfully !";
          this.userService.logout();
          setTimeout(() => {
            this.navigation.goToLogin();
          },1000);
        },
        error: e => {

          this.succeed = false;
          this.error = "Did not deleted ! " + e.error;
        }
      });

  }
  changing(){
    this.error="";
  }

  backButtonNav(){
    this.location.back();
  }

  updater(){
    this.error = '';
    this.update = true;
  }
  deleter(){
    this.error = '';
    this.update = false;
  }

  checking() : boolean{

    const password  : String = this.updateHandling.value.newPassword as String;
    const passwordV : String = this.updateHandling.value.confirmNewPassword as String;
    if((password != "") && (passwordV != "")){
      return password == passwordV;
    }else {
      return false;
    }
  }

  checkingEmpty() : String{
    const password  : String = this.updateHandling.value.newPassword as String;
    const passwordV : String = this.updateHandling.value.confirmNewPassword as String;
    if ((password != "") && (passwordV != "")){
      if(passwordV == password){
        return 'greenClassBorder'
      }else{
        return 'redClassBorder'
      }
    }else {
      return '';
    }
  }

  succeededornot(){
    if(this.succeed){
      return 'greenClassFont'
    }else {
      return 'redClassFont'
    }
  }
}

