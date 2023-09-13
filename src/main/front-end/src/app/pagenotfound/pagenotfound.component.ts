import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {NavigationService} from "../services/navigation.service";

@Component({
  selector: 'app-pagenotfound',
  templateUrl: './pagenotfound.component.html',
  styleUrls: ['./pagenotfound.component.css']
})
export class PagenotfoundComponent implements OnInit{

  constructor(
    private router : Router,
    private navigation : NavigationService
  ) {}

  ngOnInit() {
    setTimeout(()=>{this.navigation.goToHome()},
      1750);
  }
}
