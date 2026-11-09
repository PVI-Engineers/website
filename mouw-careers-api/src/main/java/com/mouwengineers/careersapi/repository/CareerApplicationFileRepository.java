package com.mouwengineering.careersapi.repository;

import com.mouwengineering.careersapi.model.CareerApplicationFile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerApplicationFileRepository extends JpaRepository<CareerApplicationFile, Long> {
    Optional<CareerApplicationFile> findByIdAndApplicationId(Long id, Long applicationId);
}
