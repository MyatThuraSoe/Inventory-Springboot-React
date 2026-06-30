package com.mdevm.InventoryMgtSystem.repositories;

import com.mdevm.InventoryMgtSystem.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
