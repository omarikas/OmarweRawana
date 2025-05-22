package com.example.demo.controller;

import com.example.demo.*;
import com.example.demo.model.User;
import com.example.demo.*;
import com.example.demo.service.MailjetEmailService;
import com.example.demo.service.userservice;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("/users")
public class UserController {

    private final userservice userService;
    private final MailjetEmailService emailService;

    public UserController(userservice userService, MailjetEmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    // Static inner DTO class
    public static class RegisterRequest {
        private String email;
        private String password;
        private String name;
        private String address;
        private String preferences;

        // Default constructor is required for Jackson
        public RegisterRequest() {}

        public RegisterRequest(String email, String password, String name, String address, String preferences) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.address = address;
            this.preferences = preferences;
        }

        // Getters
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getName() { return name; }
        public String getAddress() { return address; }
        public String getPreferences() { return preferences; }
    }

    @PostMapping("/register")
    public boolean register(@RequestBody RegisterRequest r) {
        System.out.println("Received: " + r.getEmail() + ", " + r.getPassword() + ", " + r.getName());
        return userService.register(r.getEmail(), r.getPassword(), r.getName(), r.getAddress(), r.getPreferences());
    }


    // GET /users/{id} - Read user by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        Optional<User> user = userService.getUserById(id);
        if(user.isPresent()){
            return user.get();
        }
        return null;
    }

    // PUT /users/{id} - Update user details
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String preferences
    ) {
        User updatedUser = userService.updateUser(id, name, address, preferences);
        return ResponseEntity.ok(updatedUser);
    }


    @GetMapping("/send-verification")
    public int sendVerification(@RequestParam String email) {
        
            int result = userService.sendEmailVerification(email);
            return (result);
      
    }


    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
     
            String result = userService.confirmEmailVerification(token);
            return (result); // 200 OK response
        
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String email,
            @RequestParam String password
    ) {
        Map<String, Object> response = userService.login(email, password);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token.replace("Bearer ", ""));
        return ResponseEntity.ok("Logged out successfully.");
    }


//   

    @PostMapping("/checkout")
public ResponseEntity<Map<String, Object>> checkout(@RequestHeader("Authorization") String token) {
    userService.checkout(token);
    
    Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put("message", "Checkout initiated successfully");
    
    return ResponseEntity.ok(response);
}

    @PostMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancel(@RequestHeader("Authorization") String token) {
        userService.cancelCheckout(token);
        Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put("message", "Checkout canceled successfully");
    
    return ResponseEntity.ok(response);
}
    

    @GetMapping("/validateSession")
    public ResponseEntity<String> secureEndpoint(@RequestHeader("Authorization") String authHeader) {
        try {
       

            String token = authHeader;
            userService.validateSession(token);  //  this line likely throws the error
            return ResponseEntity.ok("Access granted");
        } catch (Exception ex) {
            ex.printStackTrace(); // Log the actual error to the console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/all")
    public List<User>getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return users;
        } catch (Exception e) {
           return null;
        }
    }

    @PostMapping("/products")
    public ResponseEntity<String> addProductToUserList(
            @RequestHeader("Authorization") String token,
            @RequestParam String productId) {
        userService.addProductToList(token, productId);
        return ResponseEntity.ok("Product added to user list.");
    }



    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> getUserProducts(@RequestHeader("Authorization") String token) {
        List<Map<String, Object>> products = userService.getUserProductList(token);
        return ResponseEntity.ok(products);
    }



}



