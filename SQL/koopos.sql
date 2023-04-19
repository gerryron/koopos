create database if not exists koopos;
use koopos;

create table if not exists role (
	id int not null auto_increment,
    role_name varchar(10) unique not null,
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
	primary key (id)
);

insert into role (id, role_name) values (1, 'OWNER'); 
insert into role (id, role_name) values (2, 'STAFF'); 

create table if not exists user (
	id int not null auto_increment,
    username varchar(25) unique not null,
    email varchar(100) unique not null,
    password varchar(100) not null,
    role_id int not null,
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id),
    constraint constr_user_role_fk 
		foreign key role_fk (role_id) REFERENCES role (id)
        on delete cascade on update cascade
);

create table if not exists user_detail (
	id int not null auto_increment,
    user_id int not null,
    first_name varchar(50) not null,
    last_name varchar(50) default null,
    phone_number varchar(20) not null,
    address varchar(100) default null,
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id),
    constraint constr_userDetail_user_fk 
		foreign key user_fk (user_id) REFERENCES user (id)
        on delete cascade on update cascade
);

create table if not exists product (
	id int not null auto_increment,
    barcode varchar(255) unique not null,
    product_name varchar(100) not null,
    description text,
    quantity int default 0,
    buying_price decimal(21,2),
    selling_price decimal(21,2),
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id)
);

create table if not exists category (
	id int not null auto_increment,
    name varchar(100) unique not null,
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id)
);

create table if not exists `order` (
	id int not null auto_increment,
    order_number varchar(50) unique not null,
    status varchar(10) not null default 'PENDING',
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id)
);

create table if not exists order_details (
	id int not null auto_increment,
    order_number varchar(50) not null,
    product_id int not null,
    quantity int not null,
    price decimal(21,2) not null,
    discount decimal(21,2) default 0,
    profit decimal(21,2) not null,
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id),
    constraint constr_orderDetails_order_fk 
		foreign key order_fk (order_number) REFERENCES `order` (order_number)
        on delete cascade on update cascade,
	constraint constr_orderDetail_product_fk 
		foreign key product_fk (product_id) REFERENCES product (id)
        on delete cascade on update cascade
);

create table if not exists product_categories ( 
	product_id int not null,
    category_id int not null,
    primary key (product_id, category_id),
    constraint constr_productCategories_product_fk 
		foreign key product_fk (product_id) REFERENCES product (id)
        on delete cascade on update cascade,
    constraint constr_productCategories_category_fk 
		foreign key category_fk (category_id) REFERENCES category (id)
        on delete cascade on update cascade
);