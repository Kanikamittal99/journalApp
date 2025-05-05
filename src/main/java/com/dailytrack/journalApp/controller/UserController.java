package com.dailytrack.journalApp.controller;

import com.dailytrack.journalApp.entity.User;
import com.dailytrack.journalApp.repository.UserRepository;
import com.dailytrack.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

//    @GetMapping
//    public List<User> getAllUsers() {
//        return userService.getAll();
//    }


    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        // Added Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User userToUpdate = userService.findByUserName(username);
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setPassword(user.getPassword());
        userService.saveNewUser(userToUpdate);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestBody User user) {
        // Added Authentication
        // Flow will come till here only when user is already authenticated by Spring Security (SecurityConfig).
        // hence no case of user being null will be there.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUsername(authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
