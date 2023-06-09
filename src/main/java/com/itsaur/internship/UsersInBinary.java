package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.CopyOptions;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;

public class UsersInBinary implements UsersStore{
    private Vertx vertx;
    static int i =1;

    public UsersInBinary(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Future<Void> insert(User user) {

        return vertx
                .fileSystem()
                .open("/home/kariotis@ad.itsaur.com/IdeaProjects/RevisionV1/src/main/java/RestAPI/users.bin",new OpenOptions()
                        .setAppend(true))
                .compose(v -> {
                    final byte[] usernameInBytes = user.getUsername().getBytes();
                    final byte[] passwordInBytes = user.getPassword().getBytes();
                    byte totalSize = Integer.valueOf(2 + usernameInBytes.length + passwordInBytes.length).byteValue();
                    Buffer buffer = Buffer.buffer();
                    buffer.appendByte(totalSize);
                    buffer.appendByte(Integer.valueOf(usernameInBytes.length).byteValue());
                    buffer.appendBytes(usernameInBytes);
                    buffer.appendBytes(passwordInBytes);
                    v.write(buffer);
                    v.close();
                    System.out.println("Are you talking to me? I was finished!");
                    return Future.succeededFuture();
                });
    }
}
