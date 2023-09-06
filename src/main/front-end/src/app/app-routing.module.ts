import {Input, NgModule} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from "@angular/router";
import { LoginComponent } from "./login/login.component";
import { TestingComponent } from "./testing/testing.component";
import { PagenotfoundComponent } from "./pagenotfound/pagenotfound.component";
import { HomePageComponent } from "./home-page/home-page.component";
import { homeGuard } from "./home.guard";
import {ProfileComponent} from "./profile/profile.component";
import {User} from "./user";
import {SignupComponent} from "./login/signup/signup.component";
import {SettingsComponent} from "./profile/settings/settings.component";
import {PostsComponent} from "./profile/posts/posts.component";
import {SearchComponent} from "./search/search.component";
import {AddPostComponent} from "./add-post/add-post.component";
import {PostDetailsComponent} from "./profile/posts/post-details/post-details.component";

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full'},
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent},
  { path: 'testing', component: ProfileComponent },

  { path: 'home', component: HomePageComponent,
    canActivate: [homeGuard], children:[
      {path: 'profile', component: ProfileComponent,
      children:[
        { path: 'settings', component: SettingsComponent },
        { path: 'posts', component: PostsComponent },
        { path: 'post/:id', component: PostDetailsComponent }
      ]},
      { path: 'search', component: SearchComponent },
      { path: 'addpost', component: AddPostComponent }
    ]
  },
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
export class AppRoutingModule {}
