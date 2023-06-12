package com.itsaur.internship.adminForBinary;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;

public class ReadClass {

    public static class ReadResult {
        public int currentPosition;
        public int usernameLength;
        public int totalLength;
        public String username;
        public String password;

        public boolean isEqual(String usernm, String passwd) {
            return (password.equals(passwd)) && (username.equals(usernm));
        }
    }
    private Vertx vertx;
    public ReadClass(Vertx vertx) {
        this.vertx = vertx;
    }

    public Future<ReadResult> showAll(String path){
        return vertx
                .fileSystem()
                .open( path,//"/home/kariotis@ad.itsaur.com/Downloads/u2.bin",
                        new OpenOptions())
                .compose(file ->{
                    return readNextUser(file, 0,null, null, false);
                });

    }


    public Future<ReadResult> showAllSpare(String path){
        return vertx
                .fileSystem()
                .open( path,
                        new OpenOptions())
                .compose(file ->{
                    return readNextUser(file, 0,null, null, false);
                });
    }
    private Future<ReadResult> readNextUser(AsyncFile file, final int currentPosition, String usernm, String passwd, boolean aTest) {
        return file
                .read(Buffer.buffer(), 0, currentPosition, 2)
                .map(totalSizeBuf -> {
                    ReadResult readResult = new ReadResult();

                    readResult.currentPosition = currentPosition + 2;
                    readResult.totalLength = totalSizeBuf.getBytes()[0];
                    readResult.usernameLength = totalSizeBuf.getBytes()[1];

                    return readResult;
                })
                .compose(readResult -> file
                        .read(Buffer.buffer(), 0, readResult.currentPosition, readResult.usernameLength)
                        .map(usernameBuf -> {
                            readResult.currentPosition = readResult.currentPosition + readResult.usernameLength;
                            readResult.username = new String(usernameBuf.getBytes());
                            return readResult;
                }))
                .compose(readResult -> file
                        .read(Buffer.buffer(), 0, readResult.currentPosition, readResult.totalLength - (2 + readResult.usernameLength))
                        .map(passwordBuf -> {
                            readResult.currentPosition = readResult.currentPosition + (readResult.totalLength - (2 + readResult.usernameLength));
                            readResult.password = new String(passwordBuf.getBytes());
                            return readResult;
                }))
                .compose(readResult -> {

                        System.out.println(readResult.username + " " + readResult.password);

                    if (readResult.currentPosition == file.sizeBlocking()) {
                        return Future.failedFuture(new IllegalArgumentException());
                    } else if (readResult.currentPosition == file.sizeBlocking() + 1) {
                        return Future.succeededFuture();
                    } else {
                        return readNextUser(file, readResult.currentPosition, usernm, passwd, aTest);
                    }
                });
    }

}
