import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { RouterOutlet } from "@angular/router";
import {HttpClientModule} from "@angular/common/http";
import { PagenotfoundComponent } from './pagenotfound/pagenotfound.component';
import { HomePageComponent } from './home-page/home-page.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import { ProfileComponent } from './home-page/profile/profile.component';
import { SearchComponent } from './search/search.component';
import { PostDetailsComponent } from './home-page/post-details/post-details.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import { SmallPostPresentationComponent } from './support/small-post-presentation/small-post-presentation.component';
import { FooterPagingComponent } from './support/footer-paging/footer-paging.component';
import { CommentsPanelComponent } from './support/comments-panel/comments-panel.component';
import { PostDescriptionPanelComponent } from './support/post-description-panel/post-description-panel.component';
import { FollowRelationsComponent } from './follow-relations/follow-relations.component';
import { FeedComponent } from './feed/feed.component';

@NgModule({
  declarations: [
    AppComponent,
    PagenotfoundComponent,
    HomePageComponent,
    ProfileComponent,
    PostDetailsComponent,
    SearchComponent,
    SmallPostPresentationComponent,
    FooterPagingComponent,
    CommentsPanelComponent,
    PostDescriptionPanelComponent,
    FollowRelationsComponent,
    FeedComponent


  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        RouterOutlet,
        HttpClientModule,
        BrowserAnimationsModule,
        MatIconModule,
        MatButtonModule,
        ReactiveFormsModule,
        FormsModule,
        CommonModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
