package com.librosphere.lending.mapper;

import com.librosphere.lending.dto.InventoryDto;
import com.librosphere.lending.entity.BookInventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    InventoryDto toDto(BookInventory inventory);
}
