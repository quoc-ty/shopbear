package com.shopbear.identity.repository;

import com.shopbear.identity.entity.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityRepository extends JpaRepository<Identity,Long> {
    // Identity → Entity mà Repository quản lý
    // Long     → kiểu dữ liệu của Primary Key (Identity.id)
    // Tự tạo ========================================
    //    save()
    //    findById()
    //    findAll()
    //    delete()
    //    existsById()
}