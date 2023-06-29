CREATE TABLE users
(
    userid uuid primary key not null,
    createdate timestamp,
    updatedate timestamp,
    username varchar,
    password varchar
);

CREATE TABLE posts (
    postid uuid primary key not null,
    createdate timestamp,
    updatedate timestamp,
    filename varchar,
    description varchar,
    userid uuid references users(userid)
);

CREATE TABLE comments
(
    commentid uuid primary key not null,
    createdate timestamp,
    updatedate timestamp,
    comment varchar,
    userid uuid references users(userid),
    postid uuid references posts(postid)
);