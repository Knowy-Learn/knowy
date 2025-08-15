package com.knowy.core;

import com.knowy.core.port.CategoryRepository;
import com.knowy.core.domain.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryService categoryService;

	@Test
	void given_categoriesExist_when_findAll_then_returnListOfCategories() {
		Category category1 = new Category(1, "Food");
		Category category2 = new Category(2, "Electronics");
		List<Category> categories = Arrays.asList(category1, category2);
		Mockito.when(categoryRepository.findAll())
			.thenReturn(categories);

		List<Category> result = categoryService.findAll();

		assertEquals(categories, result); // Verifica que devuelve la misma lista
		Mockito.verify(categoryRepository, Mockito.times(1))
			.findAll();
	}
}
