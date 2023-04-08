create database if not exists koopos;
use koopos;

create table if not exists inventory (
	id int not null auto_increment,
    barcode varchar(255) unique not null,
    item_name varchar(100) not null,
    description text,
    quantity int default 0,
    buying_price decimal(21,2),
    selling_price decimal(21,2),
    created_date datetime default current_timestamp,
    updated_date datetime default current_timestamp on update current_timestamp,
    primary key (id)
);

create table if not exists category (
	id int not null auto_increment,
    name varchar(100) unique not null,
    created_date datetime default current_timestamp,
    primary key (id)
);

create table if not exists transaction (
	id int not null auto_increment,
    transaction_number varchar(255) unique not null,
    amount int not null,
    total_price decimal(21,2),
    profit decimal(21,2),
    created_date datetime default current_timestamp,
    updated_date datetime default current_timestamp on update current_timestamp,
    primary key (id)
);

create table if not exists transaction_details (
	id int not null auto_increment,
    transaction_number varchar(255) not null,
    inventory_id int not null,
    amount int not null,
    price decimal(21,2),
    created_date datetime default current_timestamp,
    updated_date datetime default current_timestamp on update current_timestamp,
    primary key (id),
    constraint constr_transactionDetails_transaction_fk 
		foreign key transaction_fk (transaction_number) REFERENCES transaction (transaction_number)
        on delete cascade on update cascade
);

create table if not exists inventory_categories ( 
	inventory_id int not null,
    category_id int not null,
    primary key (inventory_id, category_id),
    constraint constr_inventoryCategories_inventory_fk 
		foreign key inventory_fk (inventory_id) REFERENCES inventory (id)
        on delete cascade on update cascade,
    constraint constr_inventoryCategories_category_fk 
		foreign key category_fk (category_id) REFERENCES category (id)
        on delete cascade on update cascade
);