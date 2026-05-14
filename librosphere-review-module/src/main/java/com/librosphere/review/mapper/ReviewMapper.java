package com.librosphere.review.mapper;

import com.librosphere.review.dto.ReviewResponse;
import com.librosphere.review.entity.BookReview;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewResponse toResponse(BookReview entity);
}
