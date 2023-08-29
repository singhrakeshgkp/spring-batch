drop table if exists books;
create table if not exists books
(
 id int PRIMARY KEY,
 title text,
 description text,
 author text
)