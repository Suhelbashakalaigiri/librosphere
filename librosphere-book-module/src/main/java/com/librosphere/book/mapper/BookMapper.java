package com.librosphere.book.mapper;

import com.librosphere.book.dto.BookDto;
import com.librosphere.book.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface BookMapper {
    BookDto toDto(Book book);
    Book toEntity(BookDto bookDto);
}
