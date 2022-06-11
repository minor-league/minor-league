create table user
(
    id bigint not null auto_increment,
    birthday date not null,
    email varchar(255) not null,
    gender varchar(255) not null,
    name varchar(30) not null,
    password varchar(255) not null,
    primary key (id)
);

alter table user
    add constraint UK_ob8kqyqqgmefl0aco34akdtpe unique (email);

