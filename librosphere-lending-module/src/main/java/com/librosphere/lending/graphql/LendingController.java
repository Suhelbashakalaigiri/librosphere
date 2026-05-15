package com.librosphere.lending.graphql;

import com.librosphere.lending.dto.*;
import com.librosphere.lending.service.LendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LendingController {

    private final LendingService lendingService;

    @MutationMapping
    public IssueResponse issueBook(@Argument IssueBookInput input) {

        return lendingService.issueBook(input);
    }

    @MutationMapping
    public IssueResponse returnBook(@Argument Long issueId) {
        return lendingService.returnBook(issueId);
    }

    @MutationMapping
    public IssueResponse renewBook(@Argument Long issueId) {
        return lendingService.renewBook(issueId);
    }

    @MutationMapping
    public List<IssueResponse> bulkIssueBooks(@Argument BulkIssueInput input) {
        return lendingService.bulkIssueBooks(input);
    }

    @QueryMapping
    public List<IssueDto> getIssuedBooksByUser(@Argument String userId) {
        return lendingService.getIssuedBooksByUser(userId);
    }

    @QueryMapping
    public List<IssueDto> getIssueHistory(@Argument Long bookId) {
        return lendingService.getIssueHistory(bookId);
    }

    @QueryMapping
    public List<IssueDto> getDueDates(@Argument String userId) {
        return lendingService.getDueDates(userId);
    }

    @MutationMapping
    public InventoryDto initializeInventory(@Argument InitializeInventoryInput input) {
        return lendingService.initializeInventory(input);
    }

    @MutationMapping
    public InventoryDto updateInventory(@Argument UpdateInventoryInput input) {
        return lendingService.updateInventory(input);
    }

    @QueryMapping
    public InventoryDto getInventoryByBookId(@Argument Long bookId) {
        return lendingService.getInventoryByBookId(bookId);
    }

    @QueryMapping
    public List<InventoryDto> getAllInventories() {
        return lendingService.getAllInventories();
    }
}
