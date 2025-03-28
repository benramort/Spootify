package com.deusto.theComitte.Spootify.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deusto.theComitte.Spootify.DTO.CreateUserDTO;
import com.deusto.theComitte.Spootify.DTO.LoginDTO;
import com.deusto.theComitte.Spootify.DTO.UserDTO;
import com.deusto.theComitte.Spootify.entity.User;
import com.deusto.theComitte.Spootify.service.UserService;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDTO userDTO) {
        try {
            userService.createUser(userDTO.name(), userDTO.email(), userDTO.password());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User already exists")) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }   
    }

    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody LoginDTO loginDTO) {
        try {
            long token = userService.login(loginDTO.email(), loginDTO.password());
            return ResponseEntity.ok(token);
        } catch (RuntimeException ex) {
            if (ex.getMessage().equals("User does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else if (ex.getMessage().equals("Incorrect password")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam long token) {
        try {
            userService.logout(token);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException ex) {
            if (ex.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
    }

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getUsers() {
        try {
            List<User> users = userService.getUsers();
            List<UserDTO> userDTOs = new ArrayList<>();
            for(User user : users) {
                userDTOs.add(user.toDTO());
            }
            return ResponseEntity.ok(userDTOs);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    

}
