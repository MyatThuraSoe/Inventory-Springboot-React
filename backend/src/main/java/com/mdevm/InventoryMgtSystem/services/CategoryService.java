package com.mdevm.InventoryMgtSystem.services;

import com.mdevm.InventoryMgtSystem.dtos.CategoryDTO;
import com.mdevm.InventoryMgtSystem.dtos.Response;

public interface CategoryService {

    Response createCategory(CategoryDTO categoryDTO);

    Response getAllCategories();

    Response getCategoryById(Long id);

    Response updateCategory(Long id, CategoryDTO categoryDTO);

    Response deleteCategory(Long id);
}
