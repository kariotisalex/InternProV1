CREATE TABLE users
(
    personid uuid primary key not null,
    username varchar(255),
    password varchar(255)
);

CREATE TABLE images (
    imageid uuid primary key not null,
    date timestamp,
    image varchar,
    description varchar,
    personid uuid references users(personid)

);
CREATE TABLE comments
(
    commentid uuid primary key not null,
    date timestamp,
    comment varchar(255),
    imageid uuid references images(imageid)
)