package ru.gb.exam.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.exam.shop.model.Category;
import ru.gb.exam.shop.model.Product;

public interface CategoryRepository extends JpaRepository<Category, Long > {

}
