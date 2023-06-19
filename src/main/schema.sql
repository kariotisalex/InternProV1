create table users
(
    personid uuid primary key not null,
    username varchar(255),
    password varchar(255)
);