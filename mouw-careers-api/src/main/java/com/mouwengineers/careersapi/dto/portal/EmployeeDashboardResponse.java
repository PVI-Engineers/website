package com.mouwengineering.careersapi.dto.portal;

import java.util.List;

public record EmployeeDashboardResponse(
        EmployeeResponse profile,
        List<ProjectResponse> projects,
        HoursSummaryResponse summary,
        List<ActivityResponse> recentActivity,
        List<TimesheetResponse> timesheets
) {
}
