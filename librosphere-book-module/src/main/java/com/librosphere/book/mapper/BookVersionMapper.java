package com.librosphere.book.mapper;

import com.librosphere.book.dto.BookVersionDto;
import com.librosphere.book.entity.BookVersion;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface BookVersionMapper {
    BookVersionDto toDto(BookVersion bookVersion);
}
