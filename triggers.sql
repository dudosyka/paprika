
create or replace trigger update_dish_ingredient_relation before update on DishIngredientModel for each row begin
    update DishModel set `cellulose` = `cellulose` - (select cellulose from IngredientModel where id = old.ingredient) * old.measure_count where id = old.dish;
    update DishModel set `cellulose` = `cellulose` + (select cellulose from IngredientModel where id = new.ingredient) * new.measure_count where id = new.dish;
end;

create or replace trigger remove_dish_ingredient_relation before delete on DishIngredientModel for each row begin
    update DishModel set `cellulose` = `cellulose` - (select cellulose from IngredientModel where id = old.ingredient) * old.measure_count where id = old.dish;
end;

create or replace trigger create_dish_ingredient_relation before insert on DishIngredientModel for each row begin
    update DishModel set `cellulose` = `cellulose` + (select cellulose from IngredientModel where id = new.ingredient) * new.measure_count where id = new.dish;
end;

DELIMITER //

create or replace procedure sub_cellulose_from_all_dishes_with_ingredient(ingredient INT, celluloseCount DOUBLE)
begin
    DECLARE dish INT;
    DECLARE measure_count INT;
    DECLARE cursor_ CURSOR FOR select dish, measure_count from DishIngredientModel where DishIngredientModel.ingredient = ingredient;
    if (select count(*) from DishIngredientModel where DishIngredientModel.ingredient = ingredient) > 0
    then
        fetchDishes: loop fetch cursor_ into dish, measure_count;
        update DishModel set DishModel.`cellulose` = DishModel.`cellulose` - (celluloseCount * measure_count) where id = dish;
        end loop;
    end if;
end;


create or replace procedure add_cellulose_to_all_dishes_with_ingredient(ingredient INT, celluloseCount DOUBLE)
begin
    DECLARE dish INT;
    DECLARE measure_count INT;
    DECLARE cursor_ CURSOR FOR select dish, measure_count from DishIngredientModel where DishIngredientModel.ingredient = ingredient;
    if (select count(*) from DishIngredientModel where DishIngredientModel.ingredient = ingredient) > 0
    then
        fetchDishes: loop fetch cursor_ into dish, measure_count;
        update DishModel set DishModel.`cellulose` = DishModel.`cellulose` + (celluloseCount * measure_count) where id = dish;
        end loop;
    end if;
end;



DELIMITER ;

create or replace trigger update_ingredient before update on IngredientModel for each row begin
    call sub_cellulose_from_all_dishes_with_ingredient(old.id, old.cellulose);
    call add_cellulose_to_all_dishes_with_ingredient(old.id, old.cellulose);
end;

create or replace trigger remove_ingredient before delete on IngredientModel for each row begin
    call sub_cellulose_from_all_dishes_with_ingredient(old.id, old.cellulose);
end;

create or replace trigger create_ingredient before insert on IngredientModel for each row begin
    call add_cellulose_to_all_dishes_with_ingredient(new.id, new.cellulose);
end;