package com.mdevm.InventoryMgtSystem.services;

import com.mdevm.InventoryMgtSystem.dtos.Response;
import com.mdevm.InventoryMgtSystem.dtos.SupplierDTO;

public interface SupplierService {

    Response addSupplier(SupplierDTO supplierDTO);

    Response updateSupplier(Long id, SupplierDTO supplierDTO);

    Response getAllSupplier();

    Response getSupplierById(Long id);

    Response deleteSupplier(Long id);

}
