import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {UserService} from "../services/user.service";
import {User} from "../services/interfaces/user";
import {HttpErrorResponse} from "@angular/common/http";
import {debounce, fromEvent, interval, Observable} from "rxjs";

@Component({
  selector: 'app-search',

  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit{
  value! : string
  users : User[] = [];

  constructor(
    private userService : UserService
  ) {}

  ngOnInit() {
    const res = fromEvent(
      document.getElementById("inputText")!,
      'keyup')

    res.pipe(
      debounce(() => interval(500)))
      .subscribe({
        next: x => {
          this.getUsers(this.value);
        },error: (err : HttpErrorResponse) => {
          console.log(err.error)
        }
      })

  }

  getUsers(username : string){
    this.users = [];
    this.userService.getUsersByUsername(username)
      .subscribe({
        next: x => {
          this.users = x;
        },error: (err : HttpErrorResponse) => {
          console.log(err.error);
        }
      });
  }



}
