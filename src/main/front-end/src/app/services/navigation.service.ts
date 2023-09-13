import { Injectable } from '@angular/core';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  constructor(
    private router : Router
  ) { }

  goToPosts(){
    this.router.navigateByUrl('/home/profile');
  }
  goToHome(){
    this.router.navigateByUrl('/home');
  }
  goToAddPost(){
    this.router.navigateByUrl('/home/addpost');
  }
  goToLogin(){
    this.router.navigateByUrl('/login')
  }
  goToPostDetail(pst : string){
    this.router.navigateByUrl(`/home/post/${pst}`);
  }
  goToSettings(){
    this.router.navigateByUrl('/home/settings');
  }
}
