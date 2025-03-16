package ru.gb.exam.shop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.gb.exam.shop.model.Category; // Явно указываем правильный импорт
import ru.gb.exam.shop.model.Product;
import ru.gb.exam.shop.repositories.CategoryRepository;
import ru.gb.exam.shop.repositories.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category electronics = new Category();
        electronics.setName("Electronics");
        categoryRepository.save(electronics); // Используем categoryRepository

        Product phone = new Product();
        phone.setName("Phone");
        phone.setPrice(new BigDecimal("1000.00"));
        phone.setAverageRating(4.5);
        phone.setCategory(electronics);
        productRepository.save(phone);

        Product laptop = new Product();
        laptop.setName("Laptop");
        laptop.setPrice(new BigDecimal("2000.00"));
        laptop.setAverageRating(3.0);
        laptop.setCategory(electronics);
        productRepository.save(laptop);

        Product tablet = new Product();
        tablet.setName("Tablet");
        tablet.setPrice(new BigDecimal("500.00"));
        tablet.setAverageRating(4.0);
        tablet.setCategory(electronics);
        productRepository.save(tablet);
    }

    @Test
    public void testFilterProductsByPrice() {
        List<Product> result = productService.filterProducts(null, new BigDecimal("700.00"), new BigDecimal("1500.00"), null, null);
        assertEquals(1, result.size());
        assertEquals("Phone", result.get(0).getName());
    }

    @Test
    public void testSortByPriceAsc() {
        List<Product> result = productService.filterProducts(null, null, null, null, "priceAsc");
        assertEquals(3, result.size());
        assertEquals("Tablet", result.get(0).getName());
        assertEquals("Phone", result.get(1).getName());
        assertEquals("Laptop", result.get(2).getName());
    }

    @Test
    public void testSortByPriceDesc() {
        List<Product> result = productService.filterProducts(null, null, null, null, "priceDesc");
        assertEquals(3, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Phone", result.get(1).getName());
        assertEquals("Tablet", result.get(2).getName());
    }

    @Test
    public void testSortByRatingAsc() {
        List<Product> result = productService.filterProducts(null, null, null, null, "ratingAsc");
        assertEquals(3, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Tablet", result.get(1).getName());
        assertEquals("Phone", result.get(2).getName());
    }

    @Test
    public void testSortByRatingDesc() {
        List<Product> result = productService.filterProducts(null, null, null, null, "ratingDesc");
        assertEquals(3, result.size());
        assertEquals("Phone", result.get(0).getName());
        assertEquals("Tablet", result.get(1).getName());
        assertEquals("Laptop", result.get(2).getName());
    }

    @Test
    public void testAddProduct() {
        Product newProduct = new Product();
        newProduct.setName("Camera");
        newProduct.setPrice(new BigDecimal("800.00"));
        Product saved = productService.addProduct(newProduct);
        assertNotNull(saved.getId());
        assertEquals("Camera", saved.getName());
    }
}