import { Injectable } from '@angular/core';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  constructor(
    private router : Router
  ) { }

  goToMyProfile(){
    this.router.navigateByUrl('/home/myprofile');
  }
  goToMyProfileString(){
    return '/home/myprofile';
  }
  goToHome(){
    this.router.navigateByUrl('/home');
  }
  goToHomeString(){
    return '/home';
  }
  goToAddPost(){
    this.router.navigateByUrl('/home/addpost');
  }
  goToAddPostString(){
    return '/home/addpost'
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
  goToSearch(){
    this.router.navigateByUrl('/home/search');
  }
  goToSearchString(){
    return '/home/search';
  }
}
