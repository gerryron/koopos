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

create table if not exists user (
	id int not null auto_increment,
    username varchar(25) unique not null,
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
    email varchar(100) unique not null,
    phone_number varchar(25) not null,
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

create table if not exists transaction (
	id int not null auto_increment,
    transaction_number varchar(255) unique not null,
    amount int not null,
    total_price decimal(21,2),
    profit decimal(21,2),
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id)
);

create table if not exists transaction_details (
	id int not null auto_increment,
    transaction_number varchar(255) not null,
    product_id int not null,
    amount int not null,
    price decimal(21,2),
    created_date datetime default current_timestamp,
    updated_date datetime on update current_timestamp,
    primary key (id),
    constraint constr_transactionDetails_transaction_fk 
		foreign key transaction_fk (transaction_number) REFERENCES transaction (transaction_number)
        on delete cascade on update cascade,
	constraint constr_transactionDetails_product_fk 
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