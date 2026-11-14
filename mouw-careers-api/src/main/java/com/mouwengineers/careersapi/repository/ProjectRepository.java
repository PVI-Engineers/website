package com.mouwengineering.careersapi.repository;

import com.mouwengineering.careersapi.model.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByCodeIgnoreCase(String code);

    List<Project> findAllByOrderByNameAsc();
}
