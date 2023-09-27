import {inject, Injectable} from '@angular/core';
import {User} from "./interfaces/user";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";



@Injectable({
  providedIn: 'root'
})
export class UserService {

  get user() : any {
    const uid = localStorage.getItem("uid");
    const username = localStorage.getItem("username");
    if (uid && username){
      const user : User = {
        uid: uid,
        username: username
      }
      return user;
    }

  }
  // user! : User
  //   = {
  //   uid:"12cfaf83-6928-4bd6-9886-a17b77c5e626",
  //   username:"asdf"
  // }
  // ;

  constructor(
    private http: HttpClient
  ) { }

  logIn(username: string, password: string){
    const body = { username: username,
                   password: password }
    localStorage.setItem("user", JSON.stringify(body));
    return this.http.post<User>("/api/user/login", body)
      ;
  }

  loggedinUser(x : User){
    localStorage.setItem("user",JSON.stringify(x))
    // this.user = x;
    return true;
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
    localStorage.clear();

    // this.user = {
    //   uid:'',
    //   username:''
    // };

    return true;
  }

  getUsersByUsername(username : string, startFrom : number, size: number): Observable<User[]>{
    const params = new HttpParams()
      .set('startFrom', startFrom)
      .set('size', size);
    return this.http.get<User[]>(`/api/user/${username}/search`,{params : params});
  }
  countUsers(username : string) : Observable<number>{
    return this.http.get<number>(`/api/user/${username}/search/count`);

  }

  getUid(){
    const data : any = localStorage.getItem('user');
    const sess = JSON.parse(data);
    console.log(sess.uid)
    return this.user.uid;
  }

  getUsername(){
    const data : any = localStorage.getItem('user');
    const sess = JSON.parse(data);
    console.log(sess.username)
    return this.user.username;
  }

  getUser() : User{
    return this.user
  }

}
