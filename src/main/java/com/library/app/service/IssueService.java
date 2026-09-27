package com.library.app.service;

import com.library.app.dao.BookDao;
import com.library.app.dao.IssueRecordDao;
import com.library.app.exception.BookNotAvailableException;
import com.library.app.exception.ResourceNotFoundException;
import com.library.app.model.Book;
import com.library.app.model.IssueRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles issuing and returning books.
 * Each operation touches both the books table and the issue_records table,
 * so methods are wrapped in a single @Transactional boundary to guarantee
 * that copy counts and issue records never drift out of sync.
 */
@Service
public class IssueService {

    private static final int LOAN_PERIOD_DAYS = 14;

    private final BookDao bookDao;
    private final IssueRecordDao issueRecordDao;

    public IssueService(BookDao bookDao, IssueRecordDao issueRecordDao) {
        this.bookDao = bookDao;
        this.issueRecordDao = issueRecordDao;
    }

    @Transactional
    public IssueRecord issueBook(Long bookId, Long memberId) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No available copies for book id: " + bookId);
        }

        int rowsUpdated = bookDao.decrementAvailableCopies(bookId);
        if (rowsUpdated == 0) {
            // Another transaction took the last copy first; rolling back keeps data consistent.
            throw new BookNotAvailableException("Book just went out of stock, please try again: " + bookId);
        }

        IssueRecord record = new IssueRecord(
                null, bookId, memberId,
                LocalDate.now(), LocalDate.now().plusDays(LOAN_PERIOD_DAYS), null, "ISSUED"
        );
        return issueRecordDao.save(record);
    }

    @Transactional
    public IssueRecord returnBook(Long issueRecordId) {
        IssueRecord record = issueRecordDao.findById(issueRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue record not found with id: " + issueRecordId));

        if ("RETURNED".equals(record.getStatus())) {
            return record;
        }

        issueRecordDao.markReturned(issueRecordId, LocalDate.now());
        bookDao.incrementAvailableCopies(record.getBookId());

        record.setReturnDate(LocalDate.now());
        record.setStatus("RETURNED");
        return record;
    }

    public List<IssueRecord> getAllIssueRecords() {
        return issueRecordDao.findAll();
    }

    public List<IssueRecord> getActiveIssuesForMember(Long memberId) {
        return issueRecordDao.findActiveByMember(memberId);
    }
}
