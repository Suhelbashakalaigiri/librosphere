package com.librosphere.lending.mapper;

import com.librosphere.lending.dto.IssueDto;
import com.librosphere.lending.entity.BookIssue;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IssueMapper {
    IssueDto toDto(BookIssue issue);
    BookIssue toEntity(IssueDto dto);
}
