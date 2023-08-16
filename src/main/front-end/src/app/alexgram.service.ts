import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";

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
    const result = this.http.post("/api/user/login", body);
    return result;
  }
}
