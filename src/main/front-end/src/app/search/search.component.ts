import {Component, inject, OnInit} from '@angular/core';
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
  usernameValue! : string
  users : User[] = [];
  user! : User;
  isAnyUserExist : boolean = false;
  resultPerPage : number = 7;
  searchPages : number[] = [];

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
          this.countResults();
          this.getUsers( 1);
        },error: (err : HttpErrorResponse) => {
          console.log(err.error)
        }
      })

  }

  countResults(){
    this.searchPages = [];
    this.userService.countUsers(this.usernameValue)
      .subscribe({
        next: amountOfCount => {
          console.log('xxx' + amountOfCount);
          if (amountOfCount % this.resultPerPage != 0) {
            for (let i = 1; i <= (amountOfCount / this.resultPerPage) + 1; i++) {
              this.searchPages.push(i);
            }
          } else {
            for (let i = 1; i <= (amountOfCount / this.resultPerPage); i++) {
              this.searchPages.push(i);
            }
          }
        }
      })
  }
  getUsers( page : number ){
    const startFrom : number = (page - 1) * this.resultPerPage;

    this.users = [];
    this.userService.getUsersByUsername(this.usernameValue, startFrom, this.resultPerPage)
      .subscribe({
        next: x => {
          this.users = x;
        },error: (err : HttpErrorResponse) => {
          console.log(err.error);
        }
      });
  }



}
