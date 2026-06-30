package com.mdevm.InventoryMgtSystem.repositories;

import com.mdevm.InventoryMgtSystem.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
