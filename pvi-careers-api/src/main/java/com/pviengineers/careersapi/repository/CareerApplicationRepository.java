package com.pviengineers.careersapi.repository;

import com.pviengineers.careersapi.model.CareerApplication;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerApplicationRepository extends JpaRepository<CareerApplication, Long> {
    @EntityGraph(attributePaths = "files")
    List<CareerApplication> findAllByOrderByCreatedAtDesc();
}
