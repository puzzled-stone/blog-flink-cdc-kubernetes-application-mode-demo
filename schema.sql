create table cdc_table
(
    id          int primary key auto_increment,
    name        varchar(32) not null,
    create_time datetime default now()
);


create table cdc_table_copy
(
    id          int primary key auto_increment,
    name        varchar(32) not null,
    create_time datetime default now()
);