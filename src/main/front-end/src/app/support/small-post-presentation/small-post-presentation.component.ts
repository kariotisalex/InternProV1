import {Component, Input} from '@angular/core';
import {Post} from "../../services/interfaces/post";
import {NavigationService} from "../../services/navigation.service";

@Component({
  selector: 'app-small-post-presentation',
  templateUrl: './small-post-presentation.component.html',
  styleUrls: ['./small-post-presentation.component.css']
})
export class SmallPostPresentationComponent {
  @Input() post! : Post;


  constructor(
    private navigation : NavigationService
  ) {}


  postNav (pst : string) : void {
    this.navigation.goToPostDetail(pst)
  }
}
