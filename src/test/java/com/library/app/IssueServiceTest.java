package com.library.app;

import com.library.app.model.IssueRecord;
import com.library.app.service.IssueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class IssueServiceTest {

    @Autowired
    private IssueService issueService;

    @Test
    void issueThenReturnBook_updatesStatusAndCopies() {
        IssueRecord issued = issueService.issueBook(1L, 1L);
        assertNotNull(issued.getId());
        assertEquals("ISSUED", issued.getStatus());

        IssueRecord returned = issueService.returnBook(issued.getId());
        assertEquals("RETURNED", returned.getStatus());
        assertNotNull(returned.getReturnDate());
    }
}
