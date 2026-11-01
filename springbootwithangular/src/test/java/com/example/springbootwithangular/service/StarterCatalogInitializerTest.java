package com.example.springbootwithangular.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.springbootwithangular.entity.Product;
import com.example.springbootwithangular.repository.ProductRepository;

class StarterCatalogInitializerTest {
	private final ProductRepository repository = mock(ProductRepository.class);
	private final StarterCatalogInitializer initializer = new StarterCatalogInitializer(repository);

	@Test
	void addsStarterProductsWhenCatalogIsEmpty() {
		when(repository.count()).thenReturn(0L);

		initializer.run();

		ArgumentCaptor<Iterable<Product>> productsCaptor = ArgumentCaptor.forClass(Iterable.class);
		verify(repository).saveAll(productsCaptor.capture());
		List<Product> products = new ArrayList<>();
		productsCaptor.getValue().forEach(products::add);
		assertThat(products).hasSize(8)
				.anyMatch(product -> product.getName().equals("City Runner Sneakers"));
	}

	@Test
	void preservesExistingCatalog() {
		when(repository.count()).thenReturn(1L);

		initializer.run();

		verify(repository, never()).saveAll(anyIterable());
	}
}
