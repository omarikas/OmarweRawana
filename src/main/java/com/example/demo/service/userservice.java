package com.example.demo.service;

import com.example.demo.feign.ProductServiceClient;
import com.example.demo.feign.TransactionServiceClient;
import com.example.demo.model.usersess;
import com.example.demo.model.User;
import com.example.demo.model.UserProductList;
import com.example.demo.repo.UserProductListRepository;
import com.example.demo.repo.userrepo;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class userservice {
    private final userrepo userrepo;
    private final UserProductListRepository listRepository;
    private final ProductServiceClient productClient;
    private final TransactionServiceClient transactionClient;
    private final SessionManager sessionManager; // Now injected as a singleton
    private final MailjetEmailService emailService;
    private final RedisTemplate<String, String> stringRedisTemplate;


              public userservice(userrepo userrepo,
                       UserProductListRepository productListRepository,
                       ProductServiceClient productServiceClient,
                       TransactionServiceClient transactionClient,
                       SessionManager sessionManager,
                       MailjetEmailService emailService,
                       RedisTemplate<String, String> stringRedisTemplate,
                       RedisTemplate<String, usersess> redisTemplate) {
        this.userrepo = userrepo;
        this.listRepository = null;
        this.productClient = null;
        this.productListRepository = productListRepository;
        this.productServiceClient = productServiceClient;
        this.transactionClient = transactionClient;
        this.sessionManager = sessionManager;
        this.emailService = emailService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisTemplate = redisTemplate;
    }















    public Boolean register(String email, String password, String name, String address, String preferences) {
        if (userrepo.findByEmail(email) != null) {

            return false;
        }

        User user = new User().withEmailandpass(email, password).withName(name);
        if(address!=null){
            user=user.withAddress(address);
        }
        if(preferences!=null){
            user=user.withPreferences(preferences);
        }
        userrepo.save(user);
        return true;
    }

    public Optional<User> getUserById(Long userId) {
        return userrepo.findById(userId);
    }

    // act as update
    public User updateUser(Long userId, String name, String address, String preferences) {
        Optional<User> user = userrepo.findById(userId);
        if (user.isPresent()) {

            if (name != null)
                user.get().setName(name);
            if (address != null)
                user.get().setAddress(address);
            if (preferences != null)
                user.get().setPreferences(preferences);

            return userrepo.save(user.get());
        }

        else {
            return null;
        }

    }

    public boolean validateEmail(String email) {
        User user = userrepo.findByEmail(email);
        if (user != null) {

            user.validateEmail();
            userrepo.save(user);

            return true;

        }

        return false;
    }

    public int sendEmailVerification(String email) {

        User user = userrepo.findByEmail(email);
        if (user == null) {
            return -1;
        }
        if (user.isValidated()) {
            return 0;
        }

        // Generate token and store in Redis
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set("verify:" + token, email, Duration.ofHours(3));

        // Build the verification link
        // String link = "http://127.0.0.1:8081/users/verify-email?token=" + token;
        String link;
        String host = System.getenv("HOST_NAME");

        if (host != null && !host.isEmpty()) {
            link = "http://" + host + ":8081/users/verify-email?token=" + token;
        } else {
            // Fallback to default
            link = "http://127.0.0.1:8081/users/verify-email?token=" + token;
        }

        // Send the email verification
        emailService.sendEmail(email, "Email Verification", "Click here to verify: " + link);

        return 1;

    }


    public String confirmEmailVerification(String token) {
        String email = stringRedisTemplate.opsForValue().get("verify:" + token);
        if (email == null) {
            return ("Invalid or expired verification link.");
        }

        User user = userrepo.findByEmail(email);


        if(user!=null){

user.validateEmail();
        userrepo.save(user);
        stringRedisTemplate.delete("verify:" + token);

        return "Email validated successfully.";
   

        }
        return "user doesnot exist";

         }

    public Map<String, Object> login(String email, String password) {
        User user = userrepo.findByEmail(email);
        if(user==null){
            return null;
        }

       
            
        if (!user.isValidated()) {
            Map<String, Object> ages = new HashMap<>();
            ages.put("error", "email not validated");
            return ages;
        }

        String token = UUID.randomUUID().toString();
        usersess session = new usersess(token, user, LocalDateTime.now().plusHours(2));
        System.out.println("Calling sessionManager.startSession()");
        sessionManager.createSession(session); // Using singleton instance

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("token", token);
        response.put("email", user.getEmail());
        return response;
    }

    public void logout(String token) {
        sessionManager.terminateSession(token);
    }

  

    private final ProductServiceClient productServiceClient;
    private final UserProductListRepository productListRepository;
    private final RedisTemplate<String, usersess> redisTemplate;

    public void addProductToList(String token, String productId) {
        SessionManager sessionManager = new SessionManager(redisTemplate);
     

        if (!sessionManager.isSessionValid(token)) {
            throw new IllegalStateException("Invalid or expired session.");
        }

        usersess session = sessionManager.fetchSession(token);
        Long userId = session.getUserId().getId();

        // Validate product existence via Feign client
        boolean productExists = productServiceClient.isValidProduct(productId);
        if (!productExists) {
            throw new IllegalArgumentException("Invalid product ID: " + productId);
        }

        // Fetch user from DB
        Optional<User> user = userrepo.findById(userId);
        if (user.isEmpty()) {
            return;
        }

        // Fetch existing user product list or create new
        List<UserProductList> userProductLists = productListRepository.findAllByUser(user.get());
        UserProductList list;
        if (userProductLists.isEmpty()) {
            // Create new list with the productId
            list = new UserProductList(user.get());
        } else {
            // Use existing list
            list = userProductLists.get(0);
          
        }

        // Add productId only if not already in the list
        if (!list.getProductIds().contains(productId)) {
            list.getProductIds().add(productId);
        }

        // Save updated list
        productListRepository.save(list);
    }

    public void checkout(String token) {
        usersess session = validateSession(token);
        Long userId = session.getUserId().getId();

        Optional<UserProductList> list = listRepository.findById(userId);
        if(!list.isPresent()){
return;
        }
        List<String> productIds = list.get().getProductIds();
        if (productIds == null || productIds.isEmpty()) {
            return;
        }

        // Send to transaction microservice
        transactionClient.checkout(userId, productIds);

        // Clear the list after checkout
        list.get().setProductIds(new ArrayList<>());
        listRepository.save(list.get());
    }

    public void cancelCheckout(String token) {
        usersess session = validateSession(token);
        transactionClient.cancel(session.getUserId().getId());
    }

    public usersess validateSession(String token) {
        if (!sessionManager.isSessionValid(token)) {
            return null;
        }
        return sessionManager.fetchSession(token); // Safe to retrieve again
    }

    public List<User> getAllUsers() {
        return userrepo.findAll();
    }

    public List<Map<String, Object>> getUserProductList(String token) {
        usersess session = validateSession(token);
        Long userId = session.getUserId().getId();

        User user = userrepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<UserProductList> lists = productListRepository.findAllByUser(user);
        if (lists.isEmpty()|| lists==null) {
            return Collections.emptyList();
        }

        List<String> productIds = lists.get(0).getProductIds();

        // Call product microservice using Feign client
        ResponseEntity<List<Map<String, Object>>> response = productServiceClient.getProductsByIds(productIds);

        // Return the body or empty list if null
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    public void deleteUserById(Long userId) {
        userrepo.deleteById(userId);

    }

}