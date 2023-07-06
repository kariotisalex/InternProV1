package com.itsaur.internship.comment;

import com.itsaur.internship.post.PostStore;
import com.itsaur.internship.user.UsersStore;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CommentService {
    private CommentStore commentStore;
    private PostStore postStore;
    private UsersStore usersStore;
    Vertx vertx;

    public CommentService(Vertx vertx, PostStore postStore, UsersStore usersStore,CommentStore commentStore ) {
        this.commentStore = commentStore;
        this.postStore = postStore;
        this.usersStore = usersStore;
        this.vertx = vertx;
    }

    public Future<Void> addComment(UUID userid, UUID postid, String comment){
        return Future.all(this.usersStore.findUserByUserid(userid),
                   this.postStore.findPostByPostid(postid))
                .onFailure(e -> {
                    e.printStackTrace();
                })
                .compose(res -> {
                    return this.commentStore.insert(new Comment(UUID.randomUUID(),
                            LocalDateTime.now(), comment, userid, postid));
                });
   }

   public Future<Void> changeComment(UUID userid, UUID commentid, String comment){
        return this.usersStore.findUserByUserid(userid)
                .onFailure(e -> {
                    System.out.println("findUserByUserid ");
                    e.printStackTrace();
                })
                .compose(q -> {
                    if (userid.equals(q.getUserid())) {
                        return this.commentStore.findById(commentid)
                                .onFailure(e -> {
                                    System.out.println("findById");
                                    e.printStackTrace();
                                })
                                .compose(w -> {
                                    return this.commentStore.update(new Comment(w.getCommentid(),w.getCreatedate(),
                                            LocalDateTime.now(), comment, w.getUserid(), w.getPostid()));

                                });
                    } else {
                        return Future.failedFuture(new IllegalArgumentException("userid of Comment entity and given userid are not the same!"));
                    }
                });
   }

   public Future<Void> deleteComment(UUID userid, UUID commentid){
        return this.usersStore.findUserByUserid(userid)
                .compose(res -> {
                    if (userid.equals(res.getUserid())){
                        return this.commentStore.findById(commentid)
                                .onFailure(e -> {
                                    e.printStackTrace();
                                })
                                .compose(w -> {
                                    return this.commentStore.deleteById(commentid);
                                });
                    }else {
                        return Future.failedFuture("");
                    }

                });
   }


   public Future<List<Comment>> retrieveAll(UUID commentid){
        return this.commentStore.readAllByPostid(commentid);
    }
}
