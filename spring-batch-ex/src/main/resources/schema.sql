drop table if exists books;
create table if not exists books
(
 id int PRIMARY KEY,
 title text,
 description text,
 author text,
 seller text
);

drop table if exists authors;
create table if not exists authors
(
id serial PRIMARY KEY,
name text UNIQUE
);

drop table if exists seller;
create table if not exists seller
(
id serial PRIMARY KEY,
name text UNIQUE
);