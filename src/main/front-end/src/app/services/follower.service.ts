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

  getFollowingUser(userid : string) : Observable<Follower[]>{
    return this.http.get<Follower[]>(`/api/user/${userid}/followers`);
  }

  getFollowers(userid : string) : Observable<Follower[]>{
    return this.http.get<Follower[]>(`/api/user/${userid}/following`);
  }

  getIsRelation(userid :string, followerid:string){
    return this.http.get<Follower>(`/api/user/${userid}/isRelation/${followerid}`);
  }

  setFollow(userid : string, followerid : string){
    return this.http.post(`/api/user/${userid}/follow/${followerid}`,{});
  }

  setUnfollow(userid : string, followerid : string){
    return this.http.delete(`/api/user/${userid}/follow/${followerid}`);
  }





}
