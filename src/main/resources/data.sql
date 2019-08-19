delete from Waffle_Order_Waffles;
delete from Waffle_Ingredients;
delete from Waffle;
delete from Waffle_Order;

delete from Ingredient;
insert into Ingredient (id, name, type) 
                values ('REG', 'Regular Waffle', 'WRAP');
insert into Ingredient (id, name, type) 
                values ('BEL', 'Belgium Waffle', 'WRAP');
insert into Ingredient (id, name, type) 
                values ('CRM', 'Cream', 'PROTEIN');
insert into Ingredient (id, name, type) 
                values ('ICRM', 'Ice Cream', 'PROTEIN');
insert into Ingredient (id, name, type) 
                values ('STRW', 'Strawberries', 'VEGGIES');
insert into Ingredient (id, name, type) 
                values ('BNN', 'Banana', 'VEGGIES');
insert into Ingredient (id, name, type) 
                values ('HUND', '100/1000s', 'CHEESE');
insert into Ingredient (id, name, type) 
                values ('VERM', 'Vermicielli', 'CHEESE');
insert into Ingredient (id, name, type) 
                values ('CRML', 'Caramel', 'SAUCE');
insert into Ingredient (id, name, type) 
                values ('HNY', 'Honey', 'SAUCE');
