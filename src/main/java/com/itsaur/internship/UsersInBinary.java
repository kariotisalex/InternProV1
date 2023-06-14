package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.CopyOptions;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;

import java.awt.*;

public class UsersInBinary implements UsersStore{
    private Vertx vertx;
    static int i =1; //todo remove this

    //todo should accept file path in constructor
    public UsersInBinary(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Future<Void> insert(User user) {

        return vertx
                .fileSystem()
                .open(ReadResult.getPathUser(0),new OpenOptions()
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
                    v.write(buffer); //todo returns future
                    v.close(); //todo returns future, write must finish before calling close
                    System.out.println("Are you talking to me? I was finished!");
                    return Future.succeededFuture(); //todo unnecessary, you can return close instead
                });
    }
    @Override
    public Future<User> findUser(String username) {

        return vertx
                .fileSystem()
                .open(ReadResult.getPathUser(0),
                        new OpenOptions())
                .compose(file ->{
                    return readNextUser(file, 0, username)
                            .map(c->{
                                return c.userReadResult;
                            });
                });
    }
    private static Future<ReadResult> readNextUser(AsyncFile file, final int currentPosition, String usernameSearch) {
        return file
                .read(Buffer.buffer(), 0, currentPosition, 2)
                .map(totalSizeBuf -> {
                    ReadResult readResult = new ReadResult();
                    readResult.setCurrentPosition(currentPosition + 2);
                    readResult.setTotalLength(totalSizeBuf.getBytes()[0]);
                    readResult.setUsernameLength(totalSizeBuf.getBytes()[1]);
                    return readResult;
                })
                .compose(readResult -> file
                        .read(Buffer.buffer(), 0, readResult.getCurrentPosition(), readResult.getUsernameLength())
                        .map(usernameBuf -> {
                            readResult.setCurrentPosition(readResult.getCurrentPosition() + readResult.getUsernameLength());
                            readResult.userReadResult.setUsername(new String(usernameBuf.getBytes()));
                            return readResult;
                        }))
                .compose(readResult -> file
                        .read(Buffer.buffer(), 0, readResult.getCurrentPosition(), readResult.getTotalLength() - (2 + readResult.getUsernameLength()))
                        .map(passwordBuf -> {
                            readResult.setCurrentPosition(readResult.getCurrentPosition() + (readResult.getTotalLength() - (2 + readResult.getUsernameLength())));
                            readResult.userReadResult.setPassword(new String(passwordBuf.getBytes()));
                            return readResult;
                        }))
                .compose(readResult -> {
                    System.out.println(readResult.getCurrentPosition());

                    if (readResult.userReadResult.isUsernameEqual(usernameSearch)) {
                        readResult.setUserPosition(readResult.getCurrentPosition());
                        readResult.setCurrentPosition((int) file.sizeBlocking() + 1);
                        System.out.println("Found user : " + readResult.userReadResult.getUsername());
                    }

                    if (readResult.getCurrentPosition() == file.sizeBlocking()) {
                        return Future.failedFuture(new IllegalArgumentException());
                    } else if (readResult.getCurrentPosition() == file.sizeBlocking() + 1) {
                        return Future.succeededFuture()
                                .map(q ->{
                                    return readResult; //todo succeededFuture accepts parameter no need for map
                                });
                    } else {
                        return readNextUser(file, readResult.getCurrentPosition(), usernameSearch);
                    }
                });
    }


    @Override
    public Future<Void> delete(String username) {
        Future<AsyncFile> fs = vertx.fileSystem().open(ReadResult.getPathUser(0), new OpenOptions());
        Future<AsyncFile> fs1 = vertx.fileSystem().open(ReadResult.getPathUser(1), new OpenOptions().setAppend(true));

        return fs
                .compose(file -> {
                    //todo do not pass future as a method argument and remember to close all files at the end
                    return deleteNextUser(file, fs1, 0, username, vertx);
                }).compose(file -> {
                    return Future.succeededFuture();
                });

    }
    private static Future<ReadResult> deleteNextUser(AsyncFile file, Future<AsyncFile> fs1, final int currentPosition, String usernameSearch, Vertx vertx) {
        return file
                .read(Buffer.buffer(), 0, currentPosition, 2)
                .map(totalSizeBuf -> {
                    ReadResult readResult = new ReadResult();
                    readResult.setCurrentPosition(currentPosition + 2);
                    readResult.setTotalLength(totalSizeBuf.getBytes()[0]);
                    readResult.setUsernameLength(totalSizeBuf.getBytes()[1]);
                    return readResult;
                })
                .compose(readResult -> file
                        .read(Buffer.buffer(), 0, readResult.getCurrentPosition(), readResult.getUsernameLength())
                        .map(usernameBuf -> {
                            readResult.setCurrentPosition(readResult.getCurrentPosition() + readResult.getUsernameLength());
                            readResult.userReadResult.setUsername(new String(usernameBuf.getBytes()));
                            return readResult;
                        }))
                .compose(readResult -> file
                        .read(Buffer.buffer(), 0, readResult.getCurrentPosition(), readResult.getTotalLength() - (2 + readResult.getUsernameLength()))
                        .map(passwordBuf -> {
                            readResult.setCurrentPosition(readResult.getCurrentPosition() + (readResult.getTotalLength() - (2 + readResult.getUsernameLength())));
                            readResult.userReadResult.setPassword(new String(passwordBuf.getBytes()));
                            return readResult;
                        }))
                .compose(readResult -> {
                    return fs1
                            .compose(q -> {
                                if (!readResult.userReadResult.isUsernameEqual(usernameSearch)) {
                                    System.out.println(i++);
                                    final byte[] usernameInBytes = readResult.userReadResult.getUsername().getBytes();
                                    final byte[] passwordInBytes = readResult.userReadResult.getPassword().getBytes();
                                    byte totalSize = Integer.valueOf(2 + usernameInBytes.length + passwordInBytes.length).byteValue();
                                    Buffer buffer = Buffer.buffer();
                                    buffer.appendByte(totalSize);
                                    buffer.appendByte(Integer.valueOf(usernameInBytes.length).byteValue());
                                    buffer.appendBytes(usernameInBytes);
                                    buffer.appendBytes(passwordInBytes);
                                    q.write(buffer);

                                }
                                return Future.succeededFuture();
                            }).map(r ->{
                                return readResult;
                            });
                }).compose(readResult -> {
                    System.out.println(readResult.getCurrentPosition());
                    if (readResult.getCurrentPosition() == file.sizeBlocking()) {
                        return Future.succeededFuture()
                                .compose(q ->{
                                    System.out.println("Check : success");
                                    return vertx
                                            .fileSystem()
                                            .move(ReadResult.getPathUser(1),
                                                    ReadResult.getPathUser(0),
                                                    new CopyOptions().setReplaceExisting(true));
                                }).mapEmpty();
                    }
                    else {
                        return deleteNextUser(file, fs1, readResult.getCurrentPosition(), usernameSearch, vertx);
                    }
                });
    }


    @Override
    public Future<Void> changePassword(String username, String currentPassword, String newPassword) {
        Future<AsyncFile> fs = vertx.fileSystem().open(ReadResult.getPathUser(0), new OpenOptions());
        Future<AsyncFile> fs1 = vertx.fileSystem().open(ReadResult.getPathUser(1), new OpenOptions().setAppend(true));
        return fs
                .compose(file -> {
                    return changePasswordNextUser(file, fs1, 0, username, newPassword, vertx);
                }).mapEmpty();
    }


    private static Future<ReadResult> changePasswordNextUser(AsyncFile file,Future<AsyncFile> fs1, final int currentPosition, String usernameSearch, String newPassword, Vertx vertx) {
        return file
                .read(Buffer.buffer(), 0, currentPosition, 2)
                .map(totalSizeBuf -> {
                    ReadResult readResult = new ReadResult();
                    readResult.setCurrentPosition(currentPosition + 2);
                    readResult.setTotalLength(totalSizeBuf.getBytes()[0]);
                    readResult.setUsernameLength(totalSizeBuf.getBytes()[1]);
                    return readResult;
                })
                .compose(readResult -> file
                        .read(Buffer.buffer(), 0, readResult.getCurrentPosition(), readResult.getUsernameLength())
                        .map(usernameBuf -> {
                            readResult.setCurrentPosition(readResult.getCurrentPosition() + readResult.getUsernameLength());
                            readResult.userReadResult.setUsername(new String(usernameBuf.getBytes()));
                            return readResult;
                        }))

                .compose(readResult -> file
                        .read(Buffer.buffer(), 0, readResult.getCurrentPosition(), readResult.getTotalLength() - (2 + readResult.getUsernameLength()))
                        .map(passwordBuf -> {
                            readResult.setCurrentPosition(readResult.getCurrentPosition() + (readResult.getTotalLength() - (2 + readResult.getUsernameLength())));
                            readResult.userReadResult.setPassword(new String(passwordBuf.getBytes()));
                            return readResult;
                        }))
                .compose(readResult -> {

                    return fs1
                            .compose(file1 -> {
                                System.out.println(i++);
                                if ( ! readResult.userReadResult.isUsernameEqual(usernameSearch)) {
                                    final byte[] usernameInBytes = readResult.userReadResult.getUsername().getBytes();
                                    final byte[] passwordInBytes = readResult.userReadResult.getPassword().getBytes();
                                    byte totalSize = Integer.valueOf(2 + usernameInBytes.length + passwordInBytes.length).byteValue();
                                    Buffer buffer = Buffer.buffer();
                                    buffer.appendByte(totalSize);
                                    buffer.appendByte(Integer.valueOf(usernameInBytes.length).byteValue());
                                    buffer.appendBytes(usernameInBytes);
                                    buffer.appendBytes(passwordInBytes);
                                    file1.write(buffer);
                                } else if (readResult.userReadResult.isUsernameEqual(usernameSearch)) {
                                    final byte[] usernameInBytes = readResult.userReadResult.getUsername().getBytes();
                                    final byte[] passwordInBytes = newPassword.getBytes();
                                    byte totalSize = Integer.valueOf(2 + usernameInBytes.length + passwordInBytes.length).byteValue();
                                    Buffer buffer = Buffer.buffer();
                                    buffer.appendByte(totalSize);
                                    buffer.appendByte(Integer.valueOf(usernameInBytes.length).byteValue());
                                    buffer.appendBytes(usernameInBytes);
                                    buffer.appendBytes(passwordInBytes);
                                    file1.write(buffer);
                                }
                                return Future.succeededFuture();
                            })
                            .map(r ->{
                                return readResult;
                            });
                }).compose(readResult -> {
                    System.out.println(readResult.getCurrentPosition());
                    if (readResult.getCurrentPosition() == file.sizeBlocking()) {
                        return Future.succeededFuture()
                                .compose(q ->{
                                    System.out.println("Check : success");
                                    return vertx
                                            .fileSystem()
                                            .move(ReadResult.getPathUser(1),
                                                    ReadResult.getPathUser(0),
                                                    new CopyOptions().setReplaceExisting(true));
                                }).map(o -> {
                                    return readResult;
                                });
                    }
                    else {
                        return changePasswordNextUser(file, fs1, readResult.getCurrentPosition(), usernameSearch, newPassword, vertx);
                    }
                });
    }
}
