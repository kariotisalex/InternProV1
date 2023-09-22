import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Follower} from "./interfaces/follower";

@Injectable({
  providedIn: 'root'
})
export class FollowerService {

  constructor(
    private http : HttpClient
  ) { }

  getCountFollowers(userid : string) : Observable<number>{
    return this.http.get<number>(`/api/user/${userid}/followers/count`);
  }

  getCountFollowingUser(userid : string) : Observable<number>{
    return this.http.get<number>(`/api/user/${userid}/following/count`);
  }

  getFollowers(userid : string) : Observable<Follower[]>{
    return this.http.get<Follower[]>(`/api/user/${userid}/followers`);
  }

  getFollowingUser(userid : string) : Observable<Follower[]>{
    return this.http.get<Follower[]>(`/api/user/${userid}/following`);
  }






}
