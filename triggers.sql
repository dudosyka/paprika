
create or replace procedure updateDish(dish INT, ingredientId INT, measure INT, sign SMALLINT)
begin
    update DishModel set `cellulose` = `cellulose` + sign * (select cellulose from IngredientModel where id = ingredientId) * measure where id = dish;
end;

create or replace trigger update_dish_ingredient_relation before update on DishIngredientModel for each row begin
    call updateDish(old.dish, old.ingredient, old.measure_count, -1);
    call updateDish(new.dish, new.ingredient, new.measure_count, 1);
end;

create or replace trigger remove_dish_ingredient_relation before delete on DishIngredientModel for each row begin
    call updateDish(old.dish, old.ingredient, old.measure_count, -1);
end;

create or replace trigger create_dish_ingredient_relation before insert on DishIngredientModel for each row begin
    call updateDish(new.dish, new.ingredient, new.measure_count, 1);
end;

DELIMITER //

create or replace procedure sub_cellulose_from_all_dishes_with_ingredient(ingredientId INT, celluloseCount DOUBLE)
begin
    DECLARE done INT DEFAULT 0;
    DECLARE dishId INT;
    DECLARE measure DOUBLE;
    DECLARE cursor_ CURSOR FOR select dish, measure_count from DishIngredientModel where ingredient = ingredientId;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    open cursor_;
    fetchDishes: loop fetch cursor_ into dishId, measure;
    IF done = 1 THEN
        LEAVE fetchDishes;
    END IF;
    update DishModel set DishModel.`cellulose` = DishModel.`cellulose` - (celluloseCount * measure) where id = dishId;
    end loop;
    close cursor_;
end;

create or replace procedure add_cellulose_to_all_dishes_with_ingredient(ingredientId INT, celluloseCount DOUBLE)
begin
    DECLARE done INT DEFAULT 0;
    DECLARE dishId INT;
    DECLARE measure DOUBLE;
    DECLARE cursor_ CURSOR FOR select dish, measure_count from DishIngredientModel where ingredient = ingredientId;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    open cursor_;
    fetchDishes: loop fetch cursor_ into dishId, measure;
    IF done = 1 THEN
        LEAVE fetchDishes;
    END IF;
    update DishModel set DishModel.`cellulose` = DishModel.`cellulose` + (celluloseCount * measure) where id = dishId;
    end loop;
    close cursor_;
end;

DELIMITER ;

create or replace trigger update_ingredient before update on IngredientModel for each row begin
    call sub_cellulose_from_all_dishes_with_ingredient(old.id, old.cellulose);
    call add_cellulose_to_all_dishes_with_ingredient(new.id, new.cellulose);
end;

create or replace trigger remove_ingredient before delete on IngredientModel for each row begin
    call sub_cellulose_from_all_dishes_with_ingredient(old.id, old.cellulose);
end;

create or replace trigger create_ingredient before insert on IngredientModel for each row begin
    call add_cellulose_to_all_dishes_with_ingredient(new.id, new.cellulose);
end;