create table if not exists Ingredient (
  id varchar(4) not null,
  name varchar(25) not null,
  type varchar(10) not null
);

create table if not exists Waffle (
  id identity,
  name varchar(50) not null,
  createdAt timestamp not null
);

create table if not exists Waffle_Ingredients (
  waffle bigint not null,
  ingredient varchar(4) not null
);

alter table Waffle_Ingredients
    add foreign key (waffle) references Waffle(id);
alter table Waffle_Ingredients
    add foreign key (ingredient) references Ingredient(id);

create table if not exists Waffle_Order (
	id identity,
	deliveryName varchar(50) not null,
	deliveryStreet varchar(50) not null,
	deliveryCity varchar(50) not null,
	deliveryState varchar(2) not null,
	deliveryZip varchar(10) not null,
	ccNumber varchar(16) not null,
	ccExpiration varchar(5) not null,
	ccCVV varchar(3) not null,
    placedAt timestamp not null
);

create table if not exists Waffle_Order_Waffles (
	waffleOrder bigint not null,
	waffle bigint not null
);

alter table Waffle_Order_Waffles
    add foreign key (waffleOrder) references Waffle_Order(id);
alter table Waffle_Order_Waffles
    add foreign key (waffle) references Waffle(id);
