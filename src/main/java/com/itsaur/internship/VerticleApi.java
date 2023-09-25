package com.itsaur.internship;


import com.itsaur.internship.comment.CommentService;
import com.itsaur.internship.comment.query.CommentQueryModelStore;
import com.itsaur.internship.follower.FollowerService;
import com.itsaur.internship.follower.query.FollowerQueryModelStore;
import com.itsaur.internship.post.PostService;
import com.itsaur.internship.post.query.PostQueryModelStore;
import com.itsaur.internship.user.User;
import com.itsaur.internship.user.UserService;
import com.itsaur.internship.user.query.UserQueryModelStore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

public class VerticleApi extends AbstractVerticle {

    final private UserService userService;
    final private CommentService commentService;
    final private PostService postService;
    final private FollowerService followerService;
    final private PostQueryModelStore postQueryModelStore;
    final private CommentQueryModelStore commentQueryModelStore;
    final private UserQueryModelStore userQueryModelStore;
    final private FollowerQueryModelStore followerQueryModelStore;




    public VerticleApi(
            UserService userService,
            CommentService commentService,
            PostService postService,
            FollowerService followerService,
            PostQueryModelStore postQueryModelStore,
            CommentQueryModelStore commentQueryModelStore,
            UserQueryModelStore userQueryModelStore,
            FollowerQueryModelStore followerQueryModelStore
    ) {

        this.userService = userService;
        this.commentService = commentService;
        this.postService = postService;
        this.followerService = followerService;
        this.postQueryModelStore = postQueryModelStore;
        this.commentQueryModelStore = commentQueryModelStore;
        this.userQueryModelStore = userQueryModelStore;
        this.followerQueryModelStore = followerQueryModelStore;

        System.out.println("VerticleAPI : Start! (emerged by constructor)");
    }


    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

    router
            .post("/user/login")
            .handler(BodyHandler.create())
            .handler(ctx -> {
                final JsonObject body = ctx.body().asJsonObject();
                String username = Objects.requireNonNull(
                                            body.getString("username"));
                System.out.println(username);
                String password = Objects.requireNonNull(
                                            body.getString("password"));
                System.out.println(password);

                this.userService.login(username, password)
                        .onSuccess(v -> {
                            System.out.println("Successful Login");

                            JsonObject jsonObject = new JsonObject();
                            jsonObject.put("uid" , v.userid().toString());
                            jsonObject.put("username" , v.username());
                            System.out.println(jsonObject);

                            ctx.response().setStatusCode(200).end(jsonObject.encode());
                        })
                        .onFailure(v -> {
                            System.out.println("Login fails : " + v);
                            ctx.response().setStatusCode(400).end(v.getMessage());
                        });
    });

        router
                .post("/user/register")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    try {
                        final JsonObject body = ctx.body().asJsonObject();
                        String username = Objects.requireNonNull(body.getString("username"));
                        System.out.println(username);
                        String password = Objects.requireNonNull(body.getString("password"));
                        System.out.println(password);

                        if (username.isBlank()) {
                            ctx.response().setStatusCode(400).end("Empty username");
                        } else if (password.isBlank()) {
                            ctx.response().setStatusCode(400).end("Empty password");
                        } else {
                            this.userService.register(username, password)
                                    .onSuccess(v -> {
                                        System.out.println("Your registration is successful");
                                        ctx.response().setStatusCode(200).end();
                                    })
                                    .onFailure(v -> {
                                        ctx.response().setStatusCode(400).end(v.getMessage());
                                    });
                        }
                    }catch (NullPointerException e){
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }catch(Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());

                    }

                });


        router
                .put("/user/:userid/password")
                .handler(BodyHandler.create())
                .handler(ctx ->{
                    try{
                        final JsonObject body = Objects.requireNonNull(
                                                        ctx.body().asJsonObject());

                        final UUID userid       = UUID.fromString(
                                                        Objects.requireNonNull(
                                                                ctx.pathParam("userid")));

                        final String currentPw  = Objects.requireNonNull(
                                                        body.getString("current"));

                        final String newPw      = Objects.requireNonNull(
                                                        body.getString("new"));


                        this.userService.changePassword(userid, currentPw, newPw)
                            .onSuccess(v -> {
                                System.out.println("Password changes successfully from user " + ctx.pathParam("userid"));
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(e -> {
                                System.out.println("Password changing operation fails from user " + ctx.pathParam("userid"));
                                ctx.response().setStatusCode(400).end(e.getMessage());
                            });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }catch(Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());

                    }
                });


        router
                .delete("/user/:userid")
                .handler(ctx -> {
                    try{
                    System.out.println(ctx.pathParam("userid"));
                    UUID userid = UUID.fromString(Objects.requireNonNull(ctx.pathParam("userid")));
                    this.userService.deleteByUserid(userid)
                            .onSuccess(v -> {
                                System.out.println("User :" + ctx.pathParam("userid") + " deleted successfully");
                                ctx.response().setStatusCode(200).end();
                            })
                            .onFailure(v -> {
                                v.printStackTrace();
                                ctx.response().setStatusCode(400).end(v.getMessage());
                            });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end("UUID is not correct");

                    }catch (NullPointerException ex){
                        ctx.response().setStatusCode(500).end(ex.getMessage());

                    }catch(Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());

                    }
                });






        router
                .post("/user/:userid/post")
                .handler(BodyHandler
                        .create()
                        .setBodyLimit(5000000)
                        .setUploadsDirectory(String.valueOf(Paths.get("images").toAbsolutePath()))
                )
                .handler(ctx->{
                    try {
                        FileUpload file = Objects.requireNonNull(
                                                ctx.fileUploads().get(0));
                        String description = Objects.requireNonNull(
                                                ctx.request().getParam("desc"));
                        if (file.contentType().split("/")[0].equals("image")) {
                            final String fileExt = "." + file.fileName()
                                    .split("[.]")[file.fileName()
                                    .split("[.]").length - 1];
                            final String savedFileName = file.uploadedFileName()
                                    .split("/")[file.uploadedFileName()
                                    .split("/").length - 1] + fileExt;


                            vertx.fileSystem().move(file.uploadedFileName(),
                                            file.uploadedFileName() + fileExt)
                                    .compose(w -> {
                                        return postService.addPost(UUID.fromString(ctx.pathParam("userid")), savedFileName, description)
                                                .onSuccess(f -> {
                                                    ctx.response().setStatusCode(200).end(savedFileName);
                                                })
                                                .onFailure(e -> {
                                                    ctx.response().setStatusCode(400).end(e.getMessage());
                                                });
                                    });
                        } else {
                            vertx.fileSystem().delete(file.uploadedFileName());
                            ctx.response().setStatusCode(400).end();
                        }
                    }catch(NullPointerException e){
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }catch (Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }
                });



        router
                .put("/user/:userid/post/:postid")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    try{
                    UUID userid = UUID.fromString(ctx.pathParam("userid"));
                    UUID postid = UUID.fromString(ctx.pathParam("postid"));
                    String description = ctx.body().asJsonObject().getString("desc");

                    this.postService.updatePost(userid, postid,description)
                            .onSuccess(s -> {
                                ctx.response().setStatusCode(200).end("Post description updated!");
                            })
                            .onFailure(e -> {
                                System.out.println(e);
                                ctx.response().setStatusCode(400).end(e.getMessage());
                            });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end("UUID is not correct");

                    }catch(Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());

                    }
                });


        router
                .delete("/user/:userid/post/:postid")
                .handler(ctx -> {
                    try{
                    UUID userid = UUID.fromString(ctx.pathParam("userid"));
                    UUID postid = UUID.fromString(ctx.pathParam("postid"));

                    this.postService.deletePost(userid,postid)
                            .onFailure(e -> {
                                ctx.response().setStatusCode(400).end();
                            })
                            .onSuccess(s -> {
                                ctx.response().setStatusCode(200).end();
                            });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end("UUID is not correct");

                    }catch(Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());

                    }
                });






        // Comment

        router
                .post("/user/:userid/comment/:postid")
                .handler(BodyHandler.create())
                .handler(ctx ->{
                    try{
                    UUID userid    = UUID.fromString(
                                            Objects.requireNonNull(
                                            ctx.pathParam("userid")));
                    UUID postid    = UUID.fromString(
                                            Objects.requireNonNull(
                                            ctx.pathParam("postid")));
                    String comment = Objects.requireNonNull(
                                            ctx.body().asJsonObject().getString("comment"));

                    this.commentService.addComment(userid, postid, comment)
                            .onSuccess(s -> {
                                ctx.response().setStatusCode(200).end(comment);
                            })
                            .onFailure(e -> {
                                ctx.response().setStatusCode(400).end(e.getMessage());
                            });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end("UUID is not correct");

                    }catch(Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());

                    }
                });

        router
                .put("/user/:userid/comment/:commentid")
                .handler(BodyHandler.create())
                .handler(ctx -> {
                    try{
                        UUID userid = UUID.fromString(
                                            Objects.requireNonNull(
                                                    ctx.pathParam("userid")));
                        UUID commentid = UUID.fromString(
                                            Objects.requireNonNull(
                                                    ctx.pathParam("commentid")));
                        String comment = Objects.requireNonNull(ctx.body().asJsonObject().getString("comment"));

                    this.commentService.changeComment(userid, commentid, comment)
                            .onSuccess(s ->{
                                ctx.response().setStatusCode(200).end("The comment updated!");
                            }).onFailure(e -> {
                                e.printStackTrace();
                                ctx.response().setStatusCode(400).end("The comment is not updated!");
                            });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end("UUID is not correct");

                    }catch(Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());

                    }
                });
        router
                .delete("/user/:userid/comment/:commentid")
                .handler(ctx -> {
                    try{
                        UUID userid = UUID.fromString(ctx.pathParam("userid"));
                        UUID commentid = UUID.fromString(ctx.pathParam("commentid"));
                        String success = "Comment deleted!";
                        String failed = "Comment deleted unsuccessfully!";
                        this.commentService.deleteComment(userid, commentid)
                                .onFailure(e -> {
                                    ctx.response().setStatusCode(400).end(failed);
                                })
                                .onSuccess(s -> {
                                    ctx.response().setStatusCode(200).end(success);
                                });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end("UUID is not correct");

                    }catch(Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());

                    }
                });


        router
                .post("/user/:userid/follow/:followerid")
                .handler(ctx -> {
                    try{
                        UUID userid = UUID.fromString(ctx.pathParam("userid"));
                        UUID followerid = UUID.fromString(ctx.pathParam("followerid"));

                        this.followerService.addFollow(userid,followerid)
                                .onSuccess(suc -> {
                                    ctx.response().setStatusCode(200).end();
                                }).onFailure(err -> {
                                    ctx.response().setStatusCode(400).end();
                                });

                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }catch (Exception ex){
                        ex.printStackTrace();
                        ctx.response().setStatusCode(500).end(ex.getMessage());
                    }

                });

        router
                .delete("/user/:userid/follow/:followerid")
                .handler(ctx -> {
                    try{
                        UUID userid = UUID.fromString(ctx.pathParam("userid"));
                        UUID followerid = UUID.fromString(ctx.pathParam("followerid"));

                        this.followerService.deleteFollow(userid,followerid)
                                .onSuccess(suc -> {
                                    ctx.response().setStatusCode(200).end();
                                }).onFailure(err -> {
                                    ctx.response().setStatusCode(400).end();
                                });

                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }catch (Exception ex){
                        ex.printStackTrace();
                        ctx.response().setStatusCode(500).end(ex.getMessage());
                    }

                });
        router
                .get("/user/:userid/isRelation/:followerid")
                .handler(ctx -> {
                    try {
                        UUID userid = UUID.fromString(ctx.pathParam("userid"));
                        UUID followerid = UUID.fromString(ctx.pathParam("followerid"));

                        this.followerService.findRelation(userid, followerid)
                                .onSuccess(suc -> {
                                    ctx.response().setStatusCode(200).end();
                                }).onFailure(err -> {
                                    err.printStackTrace();
                                    ctx.response().setStatusCode(400).end();
                                });


                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }
                });




//         Retrieve
//         Retrieve Post

        // Post page
        router
                .get("/user/:userId/posts/page")
                .handler(ctx -> {

                        try{
                            UUID userid = UUID.fromString(Objects.requireNonNull(ctx.pathParam("userId")));
                            int startFrom = Integer.valueOf(Objects.requireNonNull(
                                                        ctx.request().getParam("startFrom")));
                            int size      = Integer.valueOf(Objects.requireNonNull(
                                                        ctx.request().getParam("size")));
                            System.out.println(startFrom + " and " + size);



                        this.postQueryModelStore.findPostPageByUid(
                                    userid,
                                    startFrom,
                                    size
                                ).onSuccess(posts -> {
                                    JsonArray jsonArray = new JsonArray();
                                    posts.forEach(re ->{
                                        jsonArray.add(new JsonObject()
                                                .put("postid"       , re.postid().toString())
                                                .put("createdDate"  , re.createdDate().toString())
                                                .put("filename"     , re.filename())
                                                .put("description"  , re.description())
                                                .put("userid"       , re.userid().toString())
                                                .put("username"     , re.username())
                                        );
                                    });
                                    ctx.response().setStatusCode(200).end(jsonArray.toString());
                                })
                                .onFailure(err -> {
                                    ctx.response().setStatusCode(400).end("There is not posts!");
                                });
                        }catch (IllegalArgumentException e){

                            e.printStackTrace();
                            ctx.response().setStatusCode(500).end(e.getMessage());

                        }catch(Exception e){
                            ctx.response().setStatusCode(500).end(e.getMessage());

                        }


                });


        // Comment page
        router
                .get("/post/:postId/comments/page")
                .handler(ctx -> {
                    String startFrom = Objects.requireNonNull(
                                                ctx.request().getParam("startFrom"));
                    String size      = Objects.requireNonNull(
                                                ctx.request().getParam("size"));

                    try{
                        this.commentQueryModelStore.findCommentPageByUid(
                                        UUID.fromString(ctx.pathParam("postId")),
                                        Integer.valueOf(startFrom),
                                        Integer.valueOf(size)
                                ).onSuccess(comments -> {
                                    JsonArray jsonArray = new JsonArray();
                                    comments.forEach(re ->{
                                        jsonArray.add(new JsonObject()
                                                .put("commentid"  ,  re.commentid().toString())
                                                .put("createdate" ,  re.createdate().toString())
                                                .put("comment"    ,  re.comment())
                                                .put("userid"     ,  re.userid().toString())
                                                .put("username"   ,  re.username())
                                                .put("postid"     ,  re.postid().toString())
                                        );
                                    });

                                    ctx.response().setStatusCode(200).end(jsonArray.toString());
                                })
                                .onFailure(err -> {
                                    err.printStackTrace();
                                    ctx.response().setStatusCode(400).end("There is no comments!");
                                });
                    }catch (NullPointerException exception){

                            exception.printStackTrace();
                            ctx.response().setStatusCode(500).end("check the null values!");

                    }catch (IllegalArgumentException e){

                            e.printStackTrace();
                            ctx.response().setStatusCode(500).end(e.getMessage());

                    }catch(Exception e){
                            ctx.response().setStatusCode(500).end(e.getMessage());

                    }

                });


        // Post count
        router
                .get("/user/:userId/posts/count")
                .handler(ctx -> {
                    try{
                        UUID userid = UUID.fromString(ctx.pathParam("userId"));
                        this.postQueryModelStore.countAllPostsbyUid(userid)
                                .onSuccess(count -> {
                                    System.out.println(count);
                                    ctx.response().setStatusCode(200).end(count);
                                })
                                .onFailure(err -> {
                                    err.printStackTrace();
                                    ctx.response().setStatusCode(400).end("0");
                                });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end("UUID is not correct");

                    }catch(Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());

                    }



                });


        // Comment count
        router
                .get("/post/:postId/comments/count")
                .handler(ctx -> {
                    try{
                        UUID postid = UUID.fromString(ctx.pathParam("postId"));
                        this.commentQueryModelStore.countAllCommentsByPid(postid)
                                .onSuccess(suc -> {
                                    ctx.response().setStatusCode(200).end(suc);
                                })
                                .onFailure(err -> {
                                    ctx.response().setStatusCode(400).end("0");
                                });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end("UUID is not correct");

                    }catch(Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());

                    }

                });




        // Retrieve post
        router
                .get("/user/:userId/post/:postid")
                .handler(ctx -> {
                    try{
                        UUID postid = UUID.fromString(ctx.pathParam("postid"));

                        this.postQueryModelStore.findById(postid)
                                .onSuccess(res -> {
                                    ctx.response().setStatusCode(200).end(
                                            new JsonObject()
                                                    .put("postid"        ,  res.postid().toString())
                                                    .put("createdDate"   ,  res.createdDate().toString())
                                                    .put("filename"      ,  res.filename())
                                                    .put("description"   ,  res.description())
                                                    .put("userid"        ,  res.userid().toString())
                                                    .put("username"      ,  res.username()
                                                    ).toString()
                                    );
                                })
                                .onFailure(err -> {
                                    ctx.response().setStatusCode(400).end();
                                });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end("UUID is not correct");

                    }catch(Exception e){
                        ctx.response().setStatusCode(500).end(e.getMessage());

                    }

                });


        // Retrieve users (for search user page)
        router
                .get("/user/:username/search")
                .handler(ctx -> {
                    try{

                        String username = ctx.pathParam("username");
                        int startFrom = Integer.valueOf(Objects.requireNonNull(
                                ctx.request().getParam("startFrom")));
                        int size      = Integer.valueOf(Objects.requireNonNull(
                                ctx.request().getParam("size")));

                        this.userQueryModelStore.findUsersPageByUsername(username, startFrom, size)
                                .onSuccess(res -> {
                                    JsonArray jsonArray = new JsonArray();
                                    res.forEach(re ->{
                                        jsonArray.add(new JsonObject()
                                                .put( "uid"      ,  re.userid().toString() )
                                                .put( "username" ,  re.username() )
                                        );
                                    });

                                    ctx.response().setStatusCode(200).end(jsonArray.toString());
                                }).onFailure(err -> {
                                    ctx.response().setStatusCode(400).end("There is no users!");

                                });
                    }catch(IllegalArgumentException ex){
                        ex.printStackTrace();
                        ctx.response().setStatusCode(500).end(ex.getMessage());
                    }catch (Exception e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }
                });


        // count users (on search page)
        router
                .get("/user/:username/search/count")
                .handler(
                        ctx -> {
                            try {
                                String username = ctx.pathParam("username");

                                this.userQueryModelStore.countAllUsersByUsername(username)
                                        .onSuccess(res -> {
                                            ctx.response().setStatusCode(200).end(res);
                                        }).onFailure(err -> {
                                            ctx.response().setStatusCode(400).end("There is no users!");

                                        });
                            } catch (IllegalArgumentException ex) {
                                ex.printStackTrace();
                                ctx.response().setStatusCode(500).end(ex.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                                ctx.response().setStatusCode(500).end(e.getMessage());
                            }
                        });

        router
                .get("/user/:userid/feed")
                .handler(ctx -> {
                    try{
                        UUID userid = UUID.fromString(Objects.requireNonNull(ctx.pathParam("userid")));
                        int startFrom = Integer.valueOf(Objects.requireNonNull(
                                ctx.request().getParam("startFrom")));
                        int size      = Integer.valueOf(Objects.requireNonNull(
                                ctx.request().getParam("size")));

                        this.postQueryModelStore.customizeFeed(userid,startFrom,size)
                                .onSuccess(posts -> {
                                    JsonArray jsonArray = new JsonArray();
                                    posts.forEach(re ->{
                                        jsonArray.add(new JsonObject()
                                                .put("postid"       , re.postid().toString())
                                                .put("createdDate"  , re.createdDate().toString())
                                                .put("filename"     , re.filename())
                                                .put("description"  , re.description())
                                                .put("userid"       , re.userid().toString())
                                                .put("username"     , re.username())
                                        );
                                    });
                                    ctx.response().setStatusCode(200).end(jsonArray.toString());
                                })
                                .onFailure(err -> {
                                    ctx.response().setStatusCode(400).end("There is not posts!");
                                });

                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }catch (Exception ex){
                        ex.printStackTrace();
                        ctx.response().setStatusCode(500).end(ex.getMessage());
                    }
                });

        // count following
        router
                .get("/user/:userid/following/count")
                .handler(ctx -> {
                    try{
                        UUID userid = UUID.fromString(Objects.requireNonNull(ctx.pathParam("userid")));

                        this.followerQueryModelStore.countfollowingUsers(userid)
                                .onSuccess(suc -> {
                                    ctx.response().setStatusCode(200).end(suc);
                                })
                                .onFailure(err -> {
                                    err.printStackTrace();
                                    ctx.response().setStatusCode(400).end(err.getMessage());
                                });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }catch (Exception er){
                        er.printStackTrace();
                        ctx.response().setStatusCode(500).end(er.getMessage());
                    }

                });

        // count followers
        router
                .get("/user/:userid/followers/count")
                .handler(ctx -> {
                    try{
                        UUID userid = UUID.fromString(Objects.requireNonNull(ctx.pathParam("userid")));

                        this.followerQueryModelStore.countFollowers(userid)
                                .onSuccess(suc -> {
                                    ctx.response().setStatusCode(200).end(suc);
                                })
                                .onFailure(err -> {
                                    err.printStackTrace();
                                    ctx.response().setStatusCode(400).end(err.getMessage());
                                });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }
                });

        router
                .get("/user/:userid/followers")
                .handler(ctx -> {
                    try{
                        UUID userid = UUID.fromString(Objects.requireNonNull(ctx.pathParam("userid")));

                        this.followerQueryModelStore.followers(userid)
                                .onSuccess(follow -> {
                                    JsonArray jsonArray = new JsonArray();
                                    follow.forEach(re ->{
                                        jsonArray.add(new JsonObject()
                                                 .put("followid"           , re.followid().toString())
                                                 .put("userid"             , re.userid().toString())
                                                 .put("usernameUserid"     , re.usernameUserid())
                                                 .put("createdate"         , re.createdate().toString())
                                                 .put("followerid"         , re.followerid().toString())
                                                 .put("followeridUsername" , re.followeridUsername())
                                        );
                                    });
                                    ctx.response().setStatusCode(200).end(jsonArray.toString());
                                })
                                .onFailure(err -> {
                                    ctx.response().setStatusCode(400).end("There is not posts!");
                                });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }catch (Exception ex){
                        ex.getMessage();
                        ctx.response().setStatusCode(500).end(ex.getMessage());
                    }
                });

        router
                .get("/user/:userid/following")
                .handler(ctx -> {
                    try{
                        UUID userid = UUID.fromString(Objects.requireNonNull(ctx.pathParam("userid")));

                        this.followerQueryModelStore.followingUsers(userid)
                                .onSuccess(following -> {
                                    JsonArray jsonArray = new JsonArray();
                                    following.forEach(re ->{
                                        jsonArray.add(new JsonObject()
                                                .put("followid"           , re.followid().toString())
                                                .put("userid"             , re.userid().toString())
                                                .put("usernameUserid"     , re.usernameUserid())
                                                .put("createdate"         , re.createdate().toString())
                                                .put("followerid"         , re.followerid().toString())
                                                .put("followeridUsername" , re.followeridUsername())
                                        );
                                    });
                                    ctx.response().setStatusCode(200).end(jsonArray.toString());
                                })
                                .onFailure(err -> {
                                    ctx.response().setStatusCode(400).end("There is not posts!");
                                });
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        ctx.response().setStatusCode(500).end(e.getMessage());
                    }catch (Exception ex){
                        ex.getMessage();
                        ctx.response().setStatusCode(500).end(ex.getMessage());
                    }
                });














        router
                .get("/post/:filename")
                .handler(ctx -> {
                    String filename = ctx.pathParam("filename");
                    vertx
                            .fileSystem()
                            .readFile(String.valueOf(Paths.get("images",filename).toAbsolutePath()))
                            .onSuccess(suc -> {
                                ctx.response().sendFile(String.valueOf(Paths.get("images",filename).toAbsolutePath()));
                            }).onFailure(err -> {
                                err.printStackTrace();
                                ctx.response().setStatusCode(404).end("Check filename!");
                            });
                });
        server.requestHandler(router).listen(8080);


    }
}

