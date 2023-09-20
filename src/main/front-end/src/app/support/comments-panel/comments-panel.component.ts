import {Component, Input, OnInit} from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import {CommentService} from "../../services/comment.service";
import {ActivatedRoute, Router} from "@angular/router";
import {NavigationService} from "../../services/navigation.service";
import {Comment} from "../../services/interfaces/comment";
import {User} from "../../services/interfaces/user";
import {Post} from "../../services/interfaces/post";

@Component({
  selector: 'app-comments-panel',
  templateUrl: './comments-panel.component.html',
  styleUrls: ['./comments-panel.component.css']
})
export class CommentsPanelComponent implements OnInit{
  @Input() user! : User;
  private cid! : string;
  @Input() post! : Post;
  comments! : Comment[];

  isUpdateCommentTabEnable : boolean = false;
  isAnyCommentExist : boolean = false;
  valuees! : string;
  commentsPerPage : number = 10;
  commentPages : number[] = [];

  constructor(
    private commentService : CommentService,
    private route          : ActivatedRoute,
    private router         : Router,
    private navigation     : NavigationService
  ) {
  }

  ngOnInit(){
    this.getComments(1);
    this.countComments();
  }


  countComments(){
    this.commentPages = [];
            this.commentService.countComments(this.post.postid)
              .subscribe({
                next: amountOfCount => {
                  console.log('xxx' + amountOfCount);
                  if (amountOfCount % this.commentsPerPage != 0) {
                    for (let i = 1; i <= (amountOfCount / this.commentsPerPage) + 1; i++) {
                      this.commentPages.push(i);
                    }
                  } else {
                    for (let i = 1; i <= (amountOfCount / this.commentsPerPage); i++) {
                      this.commentPages.push(i);
                    }
                  }
                }
              });
  }

  getComments(page : number) {
    const startFrom: number = (page - 1) * this.commentsPerPage;


            this.commentService.getPageCommentsByPostid(
                                                        this.post.postid,
                                                        startFrom,
                                                        this.commentsPerPage)
              .subscribe({
                next: listOfComments => {
                  this.isAnyCommentExist = true;
                  this.comments = listOfComments;
                },
                error: (err: HttpErrorResponse) => {
                  this.isAnyCommentExist = false;
                  this.commentPages = [0];

                }
              });

  }


  isNotificationInTextarea(){
    if(
      (this.valuees == "This comment posted successfully!")
      || (this.valuees == "This comment failed to post!")
      || (this.valuees == "Please write something to post!")
      || (this.valuees == 'The comment updated!')
      || (this.valuees == 'The comment is not updated!')
      || (this.valuees == "The comment deleted!")
      || (this.valuees == "The comment is not deleted!")
      || (this.valuees == "The comment deleted!")
      || (this.valuees == "it doesn't changed!")
      || (this.valuees == "Post description changed!")
      || (this.valuees == "Post description does not changed!")
    ){
      return true;
    }else{
      return false;
    }
  }

  clearingNewCommentTextarea() {
    if (this.isNotificationInTextarea()) {
      this.valuees = "";
    }
  }





  onSubmitNewComment() {

            if ((this.valuees != '' && this.valuees)
              && (
                !this.isNotificationInTextarea()
              )) {
              console.log(this.user.username)
              this.commentService.addNewComment(this.user.uid, this.post.postid, this.valuees)
                .subscribe({
                  next: x => {
                    this.valuees = "This comment posted successfully!";
                    this.getComments(1);
                    this.countComments();
                  },
                  error: err => {
                    this.valuees = "This comment failed to post!"
                  }
                });
            } else {
              this.valuees = "Please write something to post!"
            }
  }


// callings
  updateCommentCalling(comment : Comment){
    if(comment.userid == this.user.uid){
      this.isUpdateCommentTabEnable = true;
      this.valuees = comment.comment;
      this.cid = comment.commentid;
    }


  }







  // Buttons functions
  updateButtonFunc( comment : string ){
    this.commentService.updateComment(this.user.uid, this.cid, comment)
      .subscribe({
        next: x => {
          this.valuees = 'The comment updated!'
          this.getComments(1);
          this.countComments();
          this.backbuttonfunc();
        },
        error: err => {
          this.valuees = 'The comment is not updated!'
        }
      })
  }
  deleteCommentButtonFunc(){
    if (confirm("Are you sure?")){
      this.commentService.deleteComment(this.user.uid, this.cid)
        .subscribe({
          next: x => {
            this.valuees = "The comment deleted!"
            this.getComments(1);
            this.countComments();
            this.backbuttonfunc();
          },
          error: err => {
            this.valuees = "The comment is not deleted!"
          }
        })
    }
  }




  backbuttonfunc(){
    if(!this.isNotificationInTextarea()){
      this.valuees = '';
    }
    this.isUpdateCommentTabEnable = false;
  }

  postsNav(){
    this.navigation.goToProfile();
  }





}
