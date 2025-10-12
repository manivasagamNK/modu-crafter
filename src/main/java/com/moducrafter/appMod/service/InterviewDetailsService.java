package com.moducrafter.appMod.service;

import com.moducrafter.appMod.dto.EmployeeInterviewSummaryDTO;
import com.moducrafter.appMod.dto.InterviewDetailsRequest;
import com.moducrafter.appMod.dto.UpdateResultRequest;
import com.moducrafter.appMod.model.InterviewDetails;

import java.util.List;

public interface InterviewDetailsService {
    /**
     * Retrieves all interview records, typically sorted by date descending.
     * @return A list of all InterviewDetails records.
     */
    List<InterviewDetails> getAllInterviewDetails();

    /**
     * Creates a new InterviewDetails record based on the provided request data.
     * @param request The DTO containing the interview details, including the employee ID.
     * @return The newly created and saved InterviewDetails entity.
     */
    InterviewDetails addInterviewDetails(InterviewDetailsRequest request);

    List<EmployeeInterviewSummaryDTO> getEmployeeInterviewSummary();
    List<InterviewDetails> getInterviewsByEmployeeId(int empId);
    InterviewDetails updateInterviewResult(int interviewId, UpdateResultRequest request);
}
