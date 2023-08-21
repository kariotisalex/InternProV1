import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from "@angular/router";
import { LoginComponent } from "./login/login.component";
import { TestingComponent } from "./testing/testing.component";
import { PagenotfoundComponent } from "./pagenotfound/pagenotfound.component";
import { HomePageComponent } from "./home-page/home-page.component";
import { homeGuard } from "./home.guard";

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full'},
  { path: 'login', component: LoginComponent },
  { path: 'testing', component: TestingComponent },
  { path: 'home', component: HomePageComponent, canActivate: [homeGuard] },
  { path: '**', component: PagenotfoundComponent }
]
@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forRoot(routes)
  ],
  exports:[
    RouterModule
  ]

})
export class AppRoutingModule { }
