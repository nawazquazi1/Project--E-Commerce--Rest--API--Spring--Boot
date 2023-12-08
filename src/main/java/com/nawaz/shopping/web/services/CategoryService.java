package com.nawaz.shopping.web.services;


import com.nawaz.shopping.web.Entity.Category;
import com.nawaz.shopping.web.payloads.CategoryDTO;
import com.nawaz.shopping.web.payloads.CategoryResponse;

public interface CategoryService {

	CategoryDTO createCategory(Category category);

	CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	CategoryDTO updateCategory(Category category, Long categoryId);

	String deleteCategory(Long categoryId);
}
