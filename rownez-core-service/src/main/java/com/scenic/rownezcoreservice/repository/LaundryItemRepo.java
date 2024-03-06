package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.LaundryItem;
import com.scenic.rownezcoreservice.model.ItemType;
import org.springframework.data.repository.CrudRepository;

public interface LaundryItemRepo extends CrudRepository<LaundryItem, ItemType> {
}
