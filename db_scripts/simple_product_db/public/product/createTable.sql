create table if not exists product
(
    id                serial
    constraint product_pk
    primary key,
    code              varchar(10)    not null,
    name              varchar(64)    not null,
    price_hrk         numeric(12, 2) not null,
    price_eur         numeric(12, 2) not null,
    description       varchar(1024),
    is_available      boolean        not null,
    created_timestamp timestamp      not null,
    updated_timestamp timestamp      not null,
    deleted           timestamp
    );

alter table product
    owner to postgres;
