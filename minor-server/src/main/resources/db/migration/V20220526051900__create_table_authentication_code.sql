create table authentication_code
(
    id bigint not null auto_increment,
    authenticated bit not null,
    code char(8) not null,
    created_date_time datetime not null,
    email varchar(255) not null,
    primary key (id)
);