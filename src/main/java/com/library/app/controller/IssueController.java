package com.library.app.controller;

import com.library.app.model.IssueRecord;
import com.library.app.service.IssueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping
    public List<IssueRecord> getAllIssues() {
        return issueService.getAllIssueRecords();
    }

    @GetMapping("/member/{memberId}/active")
    public List<IssueRecord> getActiveIssuesForMember(@PathVariable Long memberId) {
        return issueService.getActiveIssuesForMember(memberId);
    }

    @PostMapping("/issue")
    public IssueRecord issueBook(@RequestParam Long bookId, @RequestParam Long memberId) {
        return issueService.issueBook(bookId, memberId);
    }

    @PostMapping("/{issueRecordId}/return")
    public IssueRecord returnBook(@PathVariable Long issueRecordId) {
        return issueService.returnBook(issueRecordId);
    }
}
