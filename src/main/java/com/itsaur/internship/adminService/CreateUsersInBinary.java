package com.itsaur.internship.adminService;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;

import java.util.Random;
import java.util.stream.IntStream;

public class CreateUsersInBinary {
    Vertx vertx;

    public CreateUsersInBinary(Vertx vertx) {
        this.vertx = vertx;
    }

    public Future<Void> generate(String path, int records) {

        return vertx
                .fileSystem()
                .open(path, new OpenOptions())
                .compose(h -> {
                    return generateRecord(h, 0, records-1);
                });
    }

    public Future<Void> generateRecord(AsyncFile file, int counter, int target){

        String letters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";

        final byte[] generatedUsername = generateRandom(letters).getBytes();
        final byte[] generatedPassword = generateRandom(numbers).getBytes();
        byte totalSize = Integer.valueOf(2 + generatedUsername.length + generatedPassword.length).byteValue();
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(totalSize);
        buffer.appendByte(Integer.valueOf(generatedUsername.length).byteValue());
        buffer.appendBytes(generatedUsername);
        buffer.appendBytes(generatedPassword);
        file.write(buffer);

        if (counter == target){
            file.close();
            return Future.succeededFuture();
        }else {

            return generateRecord(file, ++counter, target);
        }
    }

    private String generateRandom(String characters) {
        Random random = new Random();

        int size = random.nextInt(10, 20);
        StringBuilder builder = new StringBuilder();

        IntStream.range(0, size)
                .forEach(i -> {
                    int character = random.nextInt(0, characters.length());
                    builder.append(characters.charAt(character));
                });

        return builder.toString();
    }
}
