import {inject, Injectable} from '@angular/core';
import {User} from "./user";
import {HttpClient} from "@angular/common/http";



@Injectable({
  providedIn: 'root'
})
export class UserService {

  user: User = {
    uid:"000",
    username:"user"
  };

  constructor(
    private http: HttpClient
  ) { }

  logIn(username: String, password: String){
    const body = { username: username,
                   password: password }
    return this.http.post<User>("/api/user/login", body);
  }

  loggedinUser(x : User){
    this.user = x;
  }
  getUid(){
    return this.user.uid;
  }
  getUsername(){
    return this.user.username;
  }
  getUser(){
    return this.user;
  }
}
