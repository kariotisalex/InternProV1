import {inject, Injectable} from '@angular/core';
import {User} from "./interfaces/user";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";



@Injectable({
  providedIn: 'root'
})
export class UserService {

  user : User
    = {
    uid:"12cfaf83-6928-4bd6-9886-a17b77c5e626",
    username:"asdf"
  }
  ;

  constructor(
    private http: HttpClient
  ) { }

  logIn(username: string, password: string){
    const body = { username: username,
                   password: password }
    return this.http.post<User>("/api/user/login", body);
  }

  loggedinUser(x : User){
    this.user = x;
  }

  signup(username: string, password: string){
    const body = { username: username,
                   password: password }
    return this.http.post("/api/user/register", body);
  }

  changePassword(uid : string, currentPassword : string, newPassword : string){
    const body = { current : currentPassword,
                   new     : newPassword     }
    return this.http.put(`/api/user/${uid}/password`, body);
  }

  delete(uid : string){
    return this.http.delete(`/api/user/${uid}`);

  }

  logout(): boolean{
    this.user = {
      uid:'',
      username:''
    };
    return true;
  }

  getUsersByUsername(username : string): Observable<User[]>{
    return this.http.get<User[]>(`/api/user/${username}/search`);
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
