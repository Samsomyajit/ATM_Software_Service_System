create database atmdb;

use atmdb;

create table customer( 
id bigint primary key,
name varchar(30) not null,
pin varchar(10) not null,
balance bigint not null);

insert into customer values
(1, 'Abadhesh Mishra', '0123', 100),
(2, 'Somyajit Chakraborty', '0123', 200),
(3, 'John Rambo', '0123', 3000),
(4, 'Carl Johnson', '0123', 400),
(5, 'I Am Moron', '0123', 500);

desc customer;

select * from customer;


