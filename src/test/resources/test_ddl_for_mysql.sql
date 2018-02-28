drop database dbe_test;

create database dbe_test default character;

grant all on dbe_test.* to dbe@"%" identified by 'sample';
grant all on dbe_test.* to dbe@localhost identified by 'sample';

create table 