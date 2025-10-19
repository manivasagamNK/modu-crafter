package com.moducrafter.appMod.service;

import com.moducrafter.appMod.dto.EmployeeInterviewSummaryDTO;
import com.moducrafter.appMod.dto.InterviewDetailsRequest;
import com.moducrafter.appMod.dto.UpdateResultRequest;
import com.moducrafter.appMod.model.InterviewDetails;

import java.util.List;

public interface InterviewDetailsService {

    InterviewDetails addInterviewDetails(InterviewDetailsRequest request);
    InterviewDetails saveNewInterview(InterviewDetails interviewDetails);
    List<EmployeeInterviewSummaryDTO> getEmployeeInterviewSummary();
    List<InterviewDetails> getInterviewsByEmployeeId(int empId);
    InterviewDetails updateInterviewResult(int interviewId, UpdateResultRequest request);
}
