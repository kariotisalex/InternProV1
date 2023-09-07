import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { TestingComponent } from './testing/testing.component';
import { RouterOutlet } from "@angular/router";
import {HttpClientModule} from "@angular/common/http";
import { PagenotfoundComponent } from './pagenotfound/pagenotfound.component';
import { HomePageComponent } from './home-page/home-page.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import { ProfileComponent } from './profile/profile.component';
import { SignupComponent } from './login/signup/signup.component';
import { SettingsComponent } from './profile/settings/settings.component';
import { PostsComponent } from './profile/posts/posts.component';
import { SearchComponent } from './search/search.component';
import { AddPostComponent } from './add-post/add-post.component';
import { PostDetailsComponent } from './profile/posts/post-details/post-details.component';
import {ReactiveFormsModule} from "@angular/forms";

@NgModule({
  declarations: [
    AppComponent,
    PagenotfoundComponent,
    HomePageComponent,
    ProfileComponent,
    PostsComponent,
    SearchComponent,
    PostDetailsComponent

  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        RouterOutlet,
        HttpClientModule,
        BrowserAnimationsModule,
        MatIconModule,
        MatButtonModule,
        ReactiveFormsModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
