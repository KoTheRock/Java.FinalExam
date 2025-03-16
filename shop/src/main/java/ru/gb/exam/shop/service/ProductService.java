package ru.gb.exam.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gb.exam.shop.model.Product;
import ru.gb.exam.shop.repositories.ProductRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> filterProducts(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String name, String sortBy) {
        List<Product> filteredProducts = productRepository.findByFilters(categoryId, minPrice, maxPrice, name);

        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "ratingasc":
                    filteredProducts.sort(Comparator.comparing(Product::getAverageRating));
                    break;
                case "ratingdesc":
                    filteredProducts.sort(Comparator.comparing(Product::getAverageRating, Comparator.reverseOrder()));
                    break;
                case "priceasc":
                    filteredProducts.sort(Comparator.comparing(Product::getPrice));
                    break;
                case "pricedesc":
                    filteredProducts.sort(Comparator.comparing(Product::getPrice, Comparator.reverseOrder()));
                    break;
                default:
                    break;
            }
        }
        return filteredProducts;
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }
}