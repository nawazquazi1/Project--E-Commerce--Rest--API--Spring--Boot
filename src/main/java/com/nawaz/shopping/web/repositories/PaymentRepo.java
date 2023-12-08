package com.nawaz.shopping.web.repositories;

import com.nawaz.shopping.web.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long>{

}
