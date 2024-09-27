package com.example.rcmd_sys.repository;


import com.example.rcmd_sys.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByStoreId(Long storeId);
}
