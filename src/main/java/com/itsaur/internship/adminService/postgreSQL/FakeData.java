package com.itsaur.internship.adminService.postgreSQL;

import net.datafaker.Faker;

public class FakeData {



    public static void main(String[] args) {
        Faker faker = new Faker();

        String username = faker.name().username();



    }



}
