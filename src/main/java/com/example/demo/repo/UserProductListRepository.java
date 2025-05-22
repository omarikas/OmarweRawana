package com.example.demo.repo;

import com.example.demo.model.User;
import com.example.demo.model.UserProductList;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProductListRepository extends JpaRepository<UserProductList, Long> {

    // Find all UserProductLists for a given user
    List<UserProductList> findAllByUser(User user);

    // Find a UserProductList by user and check if productId exists in productIds collection
    @Query("SELECT uplist FROM UserProductList uplist WHERE uplist.user = :user AND :productId MEMBER OF uplist.productIds")
    Optional<UserProductList> findOneByUserAndProductId(@Param("user") User user, @Param("productId") String productId);

    // Check existence of UserProductList with given user and productId
    @Query("SELECT COUNT(uplist) > 0 FROM UserProductList uplist WHERE uplist.user = :user AND :productId MEMBER OF uplist.productIds")
    boolean existsByUserAndProductId(@Param("user") User user, @Param("productId") String productId);

    // Delete UserProductList entries matching user and productId
    @Modifying
    @Transactional
    @Query("DELETE FROM UserProductList uplist WHERE uplist.user = :user AND :productId MEMBER OF uplist.productIds")
    void removeByUserAndProductId(@Param("user") User user, @Param("productId") String productId);
}
