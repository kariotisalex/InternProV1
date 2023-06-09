package com.itsaur.internship;

public class User {
        private String username;
        private String password;

        public User() {
        }

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isUsernameEqual(String username) {
            return this.username.equals(username);
        }

        public boolean isPasswordEqual(String password) {
            return (this.password.equals(password));
        }
    }

