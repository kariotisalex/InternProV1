package com.itsaur.internship.follower;

import io.vertx.core.Future;

import java.time.OffsetDateTime;
import java.util.UUID;

public class FollowerService {

    private FollowerStore followerStore;

    public FollowerService(FollowerStore followerStore) {
        this.followerStore = followerStore;
    }

    public Future<Void> addFollow(UUID userid, UUID followerid){
        return this.followerStore.findByUseridFollowerid(userid,followerid)
                .otherwiseEmpty()
                .compose(res ->{
                    if (res == null){
                        return this.followerStore.insert(new Follower(
                                UUID.randomUUID(),
                                userid,
                                OffsetDateTime.now(),
                                followerid
                        ));
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("There is a follow relation in "+ res.createdate()));
                    }
                });
    }

    public Future<Void> deleteFollow(UUID userid, UUID followerid){
        return this.followerStore.findByUseridFollowerid(userid,followerid)
                .otherwiseEmpty()
                .compose(res -> {
                    if (res != null){
                        return this.followerStore.delete(res);
                    }else {
                        return Future.failedFuture(new IllegalArgumentException("Can't be deleted, there is no relation!"));
                    }

                });
    }
    public Future<Void> deleteAllFollows(UUID userid){
        return this.followerStore.deleteAll(userid)
                .onFailure(err -> {
                    err.printStackTrace();
                }).mapEmpty();

    }
    public Future<Follower> findRelation(UUID userid, UUID followerid){

        return this.followerStore.findByUseridFollowerid(userid, followerid)
                .onFailure(err -> {
                    System.out.println("na xame");
                    err.printStackTrace();
                })
                .otherwiseEmpty()
                .compose(suc -> {
                    System.out.println("asdf " + suc);
                    if (suc == null){
                        return Future.failedFuture(new IllegalArgumentException("There is no relation!"));
                    }else{
                        return Future.succeededFuture(suc);
                    }
                });
    }
}
