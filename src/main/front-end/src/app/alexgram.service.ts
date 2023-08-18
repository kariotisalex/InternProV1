import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {User} from "./user";

@Injectable({
  providedIn: 'root'
})
export class AlexgramService {

  constructor(
    private http: HttpClient
  ) { }

  isLoggedIn(username: String, password: String){
    console.log("come here!")
    const body = { username: username,
                   password: password }

    return this.http.post<User>("/api/user/login", body);
  }
}
