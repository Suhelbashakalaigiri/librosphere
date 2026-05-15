package com.librosphere.lending.service;

import com.librosphere.lending.dto.*;
import java.util.List;

public interface LendingService {
    IssueResponse issueBook(IssueBookInput input);
    IssueResponse returnBook(Long issueId);
    IssueResponse renewBook(Long issueId);
    List<IssueResponse> bulkIssueBooks(BulkIssueInput input);
    
    List<IssueDto> getIssuedBooksByUser(String userId);
    List<IssueDto> getIssueHistory(Long bookId);
    List<IssueDto> getDueDates(String userId);

    InventoryDto initializeInventory(InitializeInventoryInput input);
    InventoryDto updateInventory(UpdateInventoryInput input);
    InventoryDto getInventoryByBookId(Long bookId);
    List<InventoryDto> getAllInventories();
}
