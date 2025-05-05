package com.dailytrack.journalApp.service;

import com.dailytrack.journalApp.entity.JournalEntry;
import com.dailytrack.journalApp.entity.User;
import com.dailytrack.journalApp.repository.JournalEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) {
        try {
            User user = userService.findByUserName(username);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry savedEntry = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(savedEntry);
//            user.setUsername(null);  // To test @Transactional
            // org.springframework.dao.InvalidDataAccessApiUsageException: Query failed with error code 20 with name 'IllegalOperation' and error message 'Transaction numbers are only allowed on a replica set member or mongos' on server localhost:27017; nested exception is com.mongodb.MongoQueryException: Query failed with error code 20 with name 'IllegalOperation' and error message 'Transaction numbers are only allowed on a replica set member or mongos' on server localhost:27017
            userService.saveUser(user);
        } catch (Exception e) {
            log.error("error",e);
            throw new RuntimeException("An Error occurred while saving the entry. ",e);
        }
    }

    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(String username, ObjectId myid) {
        boolean removed = false;
        try {
            User user = userService.findByUserName(username);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(myid));
            if(removed) {
                userService.saveUser(user);
                journalEntryRepository.deleteById(myid);
            }
        }catch(Exception e) {
            System.out.println(e);
            throw new RuntimeException("An error occurred while deleting by id");

        }
        return removed;

    }

}
