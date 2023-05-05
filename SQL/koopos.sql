drop database if exists koopos;
create database koopos;
use koopos;

create table if not exists roles (
	id int not null auto_increment,
    role_name varchar(10) unique not null,
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
	primary key (id)
);

insert into roles (id, role_name) values (1, 'OWNER'); 
insert into roles (id, role_name) values (2, 'STAFF'); 

create table if not exists users (
	id int not null auto_increment,
    username varchar(25) unique not null,
    email varchar(100) unique not null,
    password varchar(100) not null,
    role_id int not null,
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id),
	foreign key fk_roles_users (role_id) REFERENCES roles (id)
);

create table if not exists user_details (
	id int not null auto_increment,
    first_name varchar(50) not null,
    last_name varchar(50),
    phone_number varchar(20) not null,
    address varchar(255),
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id),
	foreign key fk_users_userDetails (id) REFERENCES users (id)
);

create table if not exists products (
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

create table if not exists categories (
	id int not null auto_increment,
    name varchar(100) unique not null,
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id)
);

create table if not exists products_categories ( 
	product_id int not null,
    category_id int not null,
    primary key (product_id, category_id),
    foreign key fk_products_productCategories (product_id) REFERENCES products (id),
	foreign key fk_categories_productCategories (category_id) REFERENCES categories (id)
);

create table if not exists orders (
	id int not null auto_increment,
    order_number varchar(50) unique not null,
    status varchar(10) not null default 'PENDING',
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id)
);

create table if not exists order_details (
	id int not null auto_increment,
    order_id int not null,
    product_id int not null,
    quantity int not null,
    price decimal(21,2) not null,
    discount decimal(21,2) default 0,
    profit decimal(21,2) not null,
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id),
	foreign key fk_orders_orderDetails (order_id) REFERENCES orders (id),
	foreign key fk_products_orderDetails (product_id) REFERENCES products (id)
);