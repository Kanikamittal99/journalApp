package com.dailytrack.journalApp.service;

import com.dailytrack.journalApp.entity.User;
import com.dailytrack.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public UserService userService;

    @Test
    public void testFindByUserName(){
        assertNotNull(userRepository.findByUsername("ram"));
    }

    @ParameterizedTest
    @ArgumentsSource(UserArgumentsProvider.class)
    public void testSaveNewUser(User user){
        assertNotNull(userService.saveNewUser(user));
    }
}
