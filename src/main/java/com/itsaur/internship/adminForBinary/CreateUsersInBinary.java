package com.itsaur.internship.adminForBinary;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.OpenOptions;

import java.util.Random;
import java.util.stream.IntStream;
public class CreateUsersInBinary {

    public static void main(String[] args) {

        String letters = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        Vertx vertx = Vertx.vertx();


        //Binary File
        vertx
                .fileSystem()
                .open("/home/kariotis@ad.itsaur.com/Downloads/u2.bin", new OpenOptions())
                .onSuccess(h -> {
                    vertx.executeBlocking(v -> {
                        IntStream.range(0, 10).forEach(i -> {
                            final byte[] generatedUsername = generateRandom(letters).getBytes();
                            final byte[] generatedPassword = generateRandom(numbers).getBytes();
                            //user size
                            //username size
                            //username data
                            //password data
                            byte totalSize = Integer.valueOf(2 + generatedUsername.length + generatedPassword.length).byteValue();
                            Buffer buffer = Buffer.buffer();
                            buffer.appendByte(totalSize);
                            buffer.appendByte(Integer.valueOf(generatedUsername.length).byteValue());
                            buffer.appendBytes(generatedUsername);
                            buffer.appendBytes(generatedPassword);
                            h.write(buffer);
                        });
                        h.close();
                        System.out.println("Finished");
                    });
                });


    }


    public static String generateRandom(String characters) {
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
