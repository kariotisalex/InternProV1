create table users
(
    personid uuid primary key not null,
    username varchar(255),
    password varchar(255)
);

CREATE TABLE images
(
    imageid uuid primary key not null,
    image varchar(255),
    personid uuid references users(personid)
)