package com.itsaur.internship;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.CopyOptions;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UsersInBinary implements UsersStore{
    private Vertx vertx;


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
                    return v.write(buffer)
                            .compose(w -> {
                                return v.close();
                            });
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
                            .map(c -> {
                                return c.userReadResult;
                            });
                });
    }
    private Future<ReadResult> readNextUser(AsyncFile file, final int currentPosition, String usernameSearch) {
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
                        return Future.succeededFuture(readResult);
                    } else {
                        return readNextUser(file, readResult.getCurrentPosition(), usernameSearch);
                    }
                });
    }


    @Override
    public Future<Void> delete(String username) {
        Future<AsyncFile> fileSource = vertx.fileSystem().open(ReadResult.getPathUser(0), new OpenOptions());
        Future<AsyncFile> fileTemp   = vertx.fileSystem().open(ReadResult.getPathUser(1), new OpenOptions().setAppend(true));

        return Future.all(fileSource,fileTemp)
                .compose(file -> {
                    return deleteNextUser(file.resultAt(0), file.resultAt(1), 0, username);
                })
                .mapEmpty();

    }
    private Future<ReadResult> deleteNextUser(AsyncFile fileSource, AsyncFile fileTemp, final int currentPosition, String usernameSearch) {
        return fileSource
                .read(Buffer.buffer(), 0, currentPosition, 2)
                .map(totalSizeBuf -> {
                    ReadResult readResult = new ReadResult();
                    readResult.setCurrentPosition(currentPosition + 2);
                    readResult.setTotalLength(totalSizeBuf.getBytes()[0]);
                    readResult.setUsernameLength(totalSizeBuf.getBytes()[1]);
                    return readResult;
                })
                .compose(readResult -> fileSource
                        .read(Buffer.buffer(), 0, readResult.getCurrentPosition(), readResult.getUsernameLength())
                        .map(usernameBuf -> {
                            readResult.setCurrentPosition(readResult.getCurrentPosition() + readResult.getUsernameLength());
                            readResult.userReadResult.setUsername(new String(usernameBuf.getBytes()));
                            return readResult;
                        }))
                .compose(readResult -> fileSource
                        .read(Buffer.buffer(), 0, readResult.getCurrentPosition(), readResult.getTotalLength() - (2 + readResult.getUsernameLength()))
                        .map(passwordBuf -> {
                            readResult.setCurrentPosition(readResult.getCurrentPosition() + (readResult.getTotalLength() - (2 + readResult.getUsernameLength())));
                            readResult.userReadResult.setPassword(new String(passwordBuf.getBytes()));
                            return readResult;
                        }))
                .compose(readResult -> {
                    return Future.succeededFuture()
                            .compose(q -> {
                                if (!readResult.userReadResult.isUsernameEqual(usernameSearch)) {
                                    final byte[] usernameInBytes = readResult.userReadResult.getUsername().getBytes();
                                    final byte[] passwordInBytes = readResult.userReadResult.getPassword().getBytes();
                                    byte totalSize = Integer.valueOf(2 + usernameInBytes.length + passwordInBytes.length).byteValue();
                                    Buffer buffer = Buffer.buffer();
                                    buffer.appendByte(totalSize);
                                    buffer.appendByte(Integer.valueOf(usernameInBytes.length).byteValue());
                                    buffer.appendBytes(usernameInBytes);
                                    buffer.appendBytes(passwordInBytes);
                                    return fileTemp.write(buffer).compose(w -> {
                                                return Future.succeededFuture(readResult);
                                            });
                                }else {
                                    return Future.succeededFuture(readResult);
                                }
                            });
                }).compose(readResult -> {
                    System.out.println(readResult.getCurrentPosition());
                    if (readResult.getCurrentPosition() == fileSource.sizeBlocking()) {
                        return Future.succeededFuture()
                                .compose(q ->{
                                    System.out.println("Check : success");
                                    return vertx
                                            .fileSystem()
                                            .move(ReadResult.getPathUser(1),
                                                    ReadResult.getPathUser(0),
                                                    new CopyOptions().setReplaceExisting(true));
                                }).compose(w -> {
                                    return Future.all(fileSource.close(),fileTemp.close())
                                            .mapEmpty();
                                });
                    }
                    else {
                        return deleteNextUser(fileSource, fileTemp, readResult.getCurrentPosition(), usernameSearch);
                    }
                });
    }


    @Override
    public Future<Void> changePassword(String username, String newPassword) {
        Future<AsyncFile> fileSource = vertx.fileSystem().open(ReadResult.getPathUser(0), new OpenOptions());
        Future<AsyncFile> fileTemp = vertx.fileSystem().open(ReadResult.getPathUser(1), new OpenOptions().setAppend(true));
        return Future.all(fileSource,fileTemp)
                .compose(file -> {
                    return changePasswordNextUser(file.resultAt(0), file.resultAt(1), 0, username, newPassword);
                }).mapEmpty();
    }


    private Future<ReadResult> changePasswordNextUser(AsyncFile fileSource, AsyncFile fileTemp, final int currentPosition, String usernameSearch, String newPassword) {
        return fileSource
                .read(Buffer.buffer(), 0, currentPosition, 2)
                .map(totalSizeBuf -> {
                    ReadResult readResult = new ReadResult();
                    readResult.setCurrentPosition(currentPosition + 2);
                    readResult.setTotalLength(totalSizeBuf.getBytes()[0]);
                    readResult.setUsernameLength(totalSizeBuf.getBytes()[1]);
                    return readResult;
                })
                .compose(readResult -> fileSource
                        .read(Buffer.buffer(), 0, readResult.getCurrentPosition(), readResult.getUsernameLength())
                        .map(usernameBuf -> {
                            readResult.setCurrentPosition(readResult.getCurrentPosition() + readResult.getUsernameLength());
                            readResult.userReadResult.setUsername(new String(usernameBuf.getBytes()));
                            return readResult;
                        }))

                .compose(readResult -> fileSource
                        .read(Buffer.buffer(), 0, readResult.getCurrentPosition(), readResult.getTotalLength() - (2 + readResult.getUsernameLength()))
                        .map(passwordBuf -> {
                            readResult.setCurrentPosition(readResult.getCurrentPosition() + (readResult.getTotalLength() - (2 + readResult.getUsernameLength())));
                            readResult.userReadResult.setPassword(new String(passwordBuf.getBytes()));
                            return readResult;
                        }))
                .compose(readResult -> {
                    return Future.succeededFuture()
                            .compose(q -> {
                                if ( ! readResult.userReadResult.isUsernameEqual(usernameSearch)) {
                                    final byte[] usernameInBytes = readResult.userReadResult.getUsername().getBytes();
                                    final byte[] passwordInBytes = readResult.userReadResult.getPassword().getBytes();
                                    byte totalSize = Integer.valueOf(2 + usernameInBytes.length + passwordInBytes.length).byteValue();
                                    Buffer buffer = Buffer.buffer();
                                    buffer.appendByte(totalSize);
                                    buffer.appendByte(Integer.valueOf(usernameInBytes.length).byteValue());
                                    buffer.appendBytes(usernameInBytes);
                                    buffer.appendBytes(passwordInBytes);
                                    return fileTemp.write(buffer)
                                            .compose(w -> {
                                                return Future.succeededFuture(readResult);
                                            });
                                }
                                else {
                                    final byte[] usernameInBytes = readResult.userReadResult.getUsername().getBytes();
                                    final byte[] passwordInBytes = newPassword.getBytes();
                                    byte totalSize = Integer.valueOf(2 + usernameInBytes.length + passwordInBytes.length).byteValue();
                                    Buffer buffer = Buffer.buffer();
                                    buffer.appendByte(totalSize);
                                    buffer.appendByte(Integer.valueOf(usernameInBytes.length).byteValue());
                                    buffer.appendBytes(usernameInBytes);
                                    buffer.appendBytes(passwordInBytes);
                                    return fileTemp.write(buffer)
                                            .compose(w -> {
                                                return Future.succeededFuture(readResult);
                                            });
                                }

                            });
                }).compose(readResult -> {
                    System.out.println(readResult.getCurrentPosition());
                    if (readResult.getCurrentPosition() == fileSource.sizeBlocking()) {
                        return Future.succeededFuture()
                                .compose(q ->{
                                    System.out.println("Check : success");
                                    return vertx
                                            .fileSystem()
                                            .move(ReadResult.getPathUser(1),
                                                    ReadResult.getPathUser(0),
                                                    new CopyOptions().setReplaceExisting(true));
                                }).map(o -> {
                                    fileSource.close();
                                    fileTemp.close();
                                    return readResult;

                                });
                    }
                    else {
                        return changePasswordNextUser(fileSource, fileTemp, readResult.getCurrentPosition(), usernameSearch, newPassword);
                    }
                });
    }
}
