import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {UserService} from "../services/user.service";
import {User} from "../services/interfaces/user";

@Component({
  selector: 'app-search',

  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent{
  value! : string
  users : User[] = [];
  constructor(
    private userService : UserService
  ) {}

  getUsers(username : string){
    this.userService.getUsersByUsername(username);
  }



}
