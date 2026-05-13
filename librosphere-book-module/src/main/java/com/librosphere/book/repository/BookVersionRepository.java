package com.librosphere.book.repository;

import com.librosphere.book.entity.BookVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookVersionRepository extends JpaRepository<BookVersion, Long> {
    
    List<BookVersion> findByBookIdOrderByVersionNumberDesc(Long bookId);
    
    boolean existsByBookIdAndVersionNumber(Long bookId, Integer versionNumber);
}
