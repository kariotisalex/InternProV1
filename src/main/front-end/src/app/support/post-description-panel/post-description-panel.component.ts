import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Post, Posts} from "../../services/interfaces/post";
import {ActivatedRoute} from "@angular/router";
import {PostService} from "../../services/post.service";

@Component({
  selector: 'app-post-description-panel',
  templateUrl: './post-description-panel.component.html',
  styleUrls: ['./post-description-panel.component.css']
})
export class PostDescriptionPanelComponent {
  @Input() uid! : string;
  @Input() post! : Posts;
  isEditable : boolean = false;

  editor! : string;
  @Output() trigger = new EventEmitter<any>();

  constructor(
    private route          : ActivatedRoute,
    private postService    : PostService,
  ) {}
  ngOnInit(){
      this.editor = this.post.description;
  }


  updateDescCalling(){
    if(this.uid == this.post.userid){
      this.editor = this.post.description;
      this.isEditable = true;
    }


  }
  updateDescButtonFunc() {
            if ((this.editor != '') && (!this.isNotificationInTextarea())) {
              this.postService.updateDesc(this.uid, this.post.postid, this.editor)
                .subscribe({
                  next: x => {
                    this.editor = "Post description changed!"
                    setTimeout(() => {
                      this.backbuttonfunc()
                    }, 1500);
                    this.trigger.emit();
                  },
                  error: err => {
                    this.editor = "Post description does not changed!"
                  }
                });
            }

  }

  isNotificationInTextarea(){
    if(
      (this.editor == "This comment posted successfully!")
      || (this.editor == "This comment failed to post!")
      || (this.editor == "Please write something to post!")
      || (this.editor == 'The comment updated!')
      || (this.editor == 'The comment is not updated!')
      || (this.editor == "The comment deleted!")
      || (this.editor == "The comment is not deleted!")
      || (this.editor == "The comment deleted!")
      || (this.editor == "it doesn't changed!")
      || (this.editor == "Post description changed!")
      || (this.editor == "Post description does not changed!")
    ){
      return true;
    }else{
      return false;
    }
  }
  backbuttonfunc(){
    if(!this.isNotificationInTextarea()){
      this.editor = '';
    }
    this.isEditable = false;
  }


  clearingNewCommentTextarea() {
    if (this.isNotificationInTextarea()) {
      this.editor = "";
    }
  }
}
