package com.librosphere.review.mapper;

import com.librosphere.review.dto.ReviewHistoryResponse;
import com.librosphere.review.entity.ReviewHistory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewHistoryMapper {
    ReviewHistoryResponse toResponse(ReviewHistory entity);
    List<ReviewHistoryResponse> toResponseList(List<ReviewHistory> entities);
}
