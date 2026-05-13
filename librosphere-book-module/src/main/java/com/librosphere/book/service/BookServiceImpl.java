package com.librosphere.book.service;

import com.librosphere.book.dto.*;
import com.librosphere.book.entity.Book;
import com.librosphere.book.entity.BookVersion;
import com.librosphere.book.enums.BookStatus;
import com.librosphere.book.exception.*;
import com.librosphere.book.mapper.BookMapper;
import com.librosphere.book.mapper.BookVersionMapper;
import com.librosphere.book.repository.BookRepository;
import com.librosphere.book.repository.BookVersionRepository;
import com.librosphere.book.state.BookStateFactory;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookVersionRepository versionRepository;
    private final BookMapper bookMapper;
    private final BookVersionMapper versionMapper;

    @Override
    @Transactional
    public BookDto createBook(CreateBookInput input) {
        if (bookRepository.existsByIsbn(input.isbn())) {
            throw new DuplicateISBNException(input.isbn());
        }

        Book book = Book.builder()
                .title(input.title())
                .isbn(input.isbn())
                .description(input.description())
                .facultyId(input.facultyId())
                .currentVersion(1)
                .status(BookStatus.DRAFT)
                .build();

        Book savedBook = bookRepository.save(book);

        BookVersion version = BookVersion.builder()
                .bookId(savedBook.getId())
                .versionNumber(1)
                .content(input.initialContent())
                .changeLog("Initial version")
                .build();

        versionRepository.save(version);

        return bookMapper.toDto(savedBook);
    }

    @Override
    @Transactional
    public BookDto updateBook(UpdateBookInput input) {
        Book book = bookRepository.findById(input.id())
                .orElseThrow(() -> new BookNotFoundException(input.id()));

        if (book.getStatus() == BookStatus.PUBLISHED) {
            throw new VersioningRequiredException();
        }

        if (input.title() != null) book.setTitle(input.title());
        if (input.description() != null) book.setDescription(input.description());

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public boolean deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public BookDto publishBook(Long id) {

        return transitionState(id, BookStatus.PUBLISHED);
    }

    @Override
    @Transactional
    public BookDto archiveBook(Long id) {
        return transitionState(id, BookStatus.ARCHIVED);
    }

    @Override
    @Transactional
    public BookDto versionBook(VersionBookInput input) {
        Book book = bookRepository.findById(input.bookId())
                .orElseThrow(() -> new BookNotFoundException(input.bookId()));

        if (book.getStatus() != BookStatus.PUBLISHED) {
            throw new InvalidBookStateException("Version control is only available for PUBLISHED books.");
        }

        int nextVersion = book.getCurrentVersion() + 1;
        
        BookVersion version = BookVersion.builder()
                .bookId(book.getId())
                .versionNumber(nextVersion)
                .content(input.content())
                .changeLog(input.changeLog())
                .build();

        versionRepository.save(version);

        book.setCurrentVersion(nextVersion);
        book.setStatus(BookStatus.DRAFT);
        
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Override
    public List<BookDto> searchBooks(BookSearchCriteria criteria) {
        Specification<Book> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria.title() != null) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.title().toLowerCase() + "%"));
            }
            if (criteria.isbn() != null) {
                predicates.add(cb.equal(root.get("isbn"), criteria.isbn()));
            }
            if (criteria.facultyId() != null) {
                predicates.add(cb.equal(root.get("facultyId"), criteria.facultyId()));
            }
            if (criteria.status() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.status()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        PageRequest pageRequest = PageRequest.of(
                criteria.page() != null ? criteria.page() : 0,
                criteria.size() != null ? criteria.size() : 10
        );

        return bookRepository.findAll(spec, pageRequest)
                .map(bookMapper::toDto)
                .getContent();
    }

    @Override
    public List<BookDto> getBooksByFaculty(Long facultyId) {
        return bookRepository.findByFacultyId(facultyId).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookVersionDto> getBookVersions(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        return versionRepository.findByBookIdOrderByVersionNumberDesc(bookId).stream()
                .map(versionMapper::toDto)
                .toList();
    }

    private BookDto transitionState(Long id, BookStatus targetStatus) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        BookStateFactory.get(book.getStatus()).validateTransition(targetStatus);
        
        book.setStatus(targetStatus);
        return bookMapper.toDto(bookRepository.save(book));
    }
}
