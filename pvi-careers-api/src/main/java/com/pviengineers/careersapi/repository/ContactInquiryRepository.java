package com.pviengineers.careersapi.repository;

import com.pviengineers.careersapi.model.ContactInquiry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactInquiryRepository extends JpaRepository<ContactInquiry, Long> {
    List<ContactInquiry> findAllByOrderByCreatedAtDesc();
}
