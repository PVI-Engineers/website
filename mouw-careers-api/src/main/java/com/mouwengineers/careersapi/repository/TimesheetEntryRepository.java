package com.mouwengineering.careersapi.repository;

import com.mouwengineering.careersapi.model.TimesheetEntry;
import com.mouwengineering.careersapi.model.TimesheetStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TimesheetEntryRepository extends JpaRepository<TimesheetEntry, Long> {

    @Query("""
            select entry
            from TimesheetEntry entry
            join fetch entry.project
            where entry.employee.id = :employeeId
            order by entry.workDate desc, entry.submittedAt desc
            """)
    List<TimesheetEntry> findForEmployee(Long employeeId);

    @Query("""
            select entry
            from TimesheetEntry entry
            join fetch entry.employee
            join fetch entry.project
            order by entry.submittedAt desc
            """)
    List<TimesheetEntry> findAllDetailed();

    @Query("""
            select entry
            from TimesheetEntry entry
            where entry.employee.id = :employeeId
              and entry.workDate = :workDate
              and entry.status <> :status
            """)
    List<TimesheetEntry> findActiveForEmployeeOnDate(Long employeeId, LocalDate workDate, TimesheetStatus status);
}
