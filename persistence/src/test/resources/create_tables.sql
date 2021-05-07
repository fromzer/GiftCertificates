create table user
(
    id         bigint auto_increment
        primary key,
    login      varchar(30) not null,
    first_name varchar(30) not null,
    last_name  varchar(50) not null,
    constraint login
        unique (login)
);

create table tag
(
    id   bigint auto_increment
        primary key,
    name varchar(50) not null,
    constraint tag_name_uindex
        unique (name)
);

create table gift_certificate
(
    id               bigint auto_increment
        primary key,
    name             varchar(50)               not null,
    description      varchar(500)              not null,
    price            decimal(10, 2)            not null,
    duration         int                       not null,
    create_date      timestamp default (now()) not null,
    last_update_date timestamp default (now()) not null
);

create table orders
(
    id            bigint auto_increment
        primary key,
    cost          decimal(10, 2)            not null,
    purchase_date timestamp default (now()) not null,
    user_id       bigint                    not null,
    constraint orders_user_id_fk
        foreign key (user_id) references user (id)
);

create table order_certificate
(
    order_id       bigint not null,
    certificate_id bigint not null,
    primary key (order_id, certificate_id),
    constraint order_certificate_gift_certificate_id_fk
        foreign key (certificate_id) references gift_certificate (id),
    constraint order_certificate_orders_id_fk
        foreign key (order_id) references orders (id)
);

create table gift_certificate_tag
(
    gift_certificate_id bigint not null,
    tag_id              bigint not null,
    primary key (tag_id, gift_certificate_id),
    constraint gift_certificate_tag_gift_certificate_id_fk
        foreign key (gift_certificate_id) references gift_certificate (id)
            on delete cascade,
    constraint gift_certificate_tag_tag_id_fk
        foreign key (tag_id) references tag (id)
            on delete cascade
);


