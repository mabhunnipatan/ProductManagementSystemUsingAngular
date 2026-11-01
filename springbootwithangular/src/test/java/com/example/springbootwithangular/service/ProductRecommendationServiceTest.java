package com.example.springbootwithangular.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.springbootwithangular.entity.Product;

class ProductRecommendationServiceTest {
	private final ProductRecommendationService recommendations = new ProductRecommendationService();

	@Test
	void ranksTypoTolerantNameMatchesBeforeDescriptionMatches() {
		Product shoes = product("Sneakers", "Comfortable running shoes", "Shoes");
		Product bag = product("Everyday bag", "Sneaker print accessory", "Accessories");

		assertThat(recommendations.recommend(List.of(bag, shoes), "sneekers"))
				.containsExactly(shoes);
	}

	@Test
	void returnsAllProductsWhenSearchIsBlank() {
		Product first = product("First", "", "Other");
		Product second = product("Second", "", "Other");
		first.setId(1L);
		second.setId(2L);

		assertThat(recommendations.recommend(List.of(first, second), "  "))
				.containsExactly(second, first);
	}

	private Product product(String name, String description, String category) {
		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setCategory(category);
		return product;
	}
}
