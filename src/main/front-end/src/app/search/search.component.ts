import {Component, OnInit} from '@angular/core';
import {UserService} from "../services/user.service";
import {User} from "../services/interfaces/user";
import {HttpErrorResponse} from "@angular/common/http";
import {debounce, fromEvent, interval, Observable} from "rxjs";
import {NavigationService} from "../services/navigation.service";

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
    private userService : UserService,
    public navigation : NavigationService
  ) {}

  ngOnInit() {
    const res = fromEvent(
      document.getElementById("inputText")!,
      'keyup')

    res.pipe(
      debounce(() => interval(500)))
      .subscribe({
        next: x => {

          this.getUsers( 1);
        },error: (err : HttpErrorResponse) => {
          console.log(err.error)
        }
      })

  }

  getUsers( page : number ){
    const startFrom : number = (page - 1) * this.resultPerPage;

    this.users = [];

    this.userService.getUsersByUsername(this.usernameValue, startFrom, this.resultPerPage)
      .subscribe({
        next: x => {
          this.searchPages = [];

          for (let user of x){

            if (user.uid){
                this.users.push(user);
            }else {
              const amountOfCount = user.username as unknown as number;
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


          }
        }
        , error: (err: HttpErrorResponse) => {
          console.log(err.error);
        }
      });
  }


}
