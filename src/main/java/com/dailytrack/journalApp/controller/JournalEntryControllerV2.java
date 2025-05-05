package com.dailytrack.journalApp.controller;

import com.dailytrack.journalApp.entity.JournalEntry;
import com.dailytrack.journalApp.entity.User;
import com.dailytrack.journalApp.service.JournalEntryService;
import com.dailytrack.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        List<JournalEntry> userJournalEntries = user.getJournalEntries();
        if(userJournalEntries != null && !userJournalEntries.isEmpty()) {
            return new ResponseEntity<>(userJournalEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            journalEntryService.saveEntry(myEntry,username);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //JSON format issues
        }
    }

    @GetMapping("/id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        List<JournalEntry> entries = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if(!entries.isEmpty()) {
            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);
            if(journalEntry.isPresent()) {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Even after deleting the journalentry, in Users, journalEntries is still pointing to deleted item.
    // cascade delete is not happening in MongoDB. it only works in Relational db.
    // hence, we need to manually delete those entries
    @DeleteMapping("/id/{myId}")
    public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean removed = journalEntryService.deleteById(username, myId);
        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<JournalEntry> updateJournalEntryById(@PathVariable ObjectId myId,
                                                               @RequestBody JournalEntry myEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        List<JournalEntry> entries = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if(!entries.isEmpty()) {
            JournalEntry old = journalEntryService.findById(myId).orElse(null);
            if(old != null) {
                old.setTitle(myEntry.getTitle() != null && !myEntry.getTitle().equals("") ? myEntry.getTitle() : old.getTitle());
                old.setContent(myEntry.getContent() != null && !myEntry.getContent().equals("") ? myEntry.getContent() : old.getContent());
                journalEntryService.saveEntry(old);
                return new ResponseEntity<>(old, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
