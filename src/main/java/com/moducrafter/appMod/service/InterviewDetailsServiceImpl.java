package com.moducrafter.appMod.service;

import com.moducrafter.appMod.dto.EmployeeInterviewSummaryDTO;
import com.moducrafter.appMod.dto.InterviewDetailsRequest;
import com.moducrafter.appMod.dto.UpdateResultRequest;
import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.model.InterviewDetails;
import com.moducrafter.appMod.repository.EmployeeRepository;
import com.moducrafter.appMod.repository.InterviewDetailsReposiorty;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InterviewDetailsServiceImpl implements InterviewDetailsService {
    @Autowired
    private InterviewDetailsReposiorty interviewDetailsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<InterviewDetails> getAllInterviewDetails() {
        // Retrieves all records, sorted by date descending (most recent first)
        return interviewDetailsRepository.findAllByOrderByInterviewDateDesc();
    }


    @Override
    public List<EmployeeInterviewSummaryDTO> getEmployeeInterviewSummary() {
        List<InterviewDetails> allInterviews = interviewDetailsRepository.findAll();

        // Group by employee and find the most recent interview for each
        Map<Employee, InterviewDetails> latestInterviewMap = allInterviews.stream()
                .collect(Collectors.toMap(
                        InterviewDetails::getEmployee,
                        interview -> interview,
                        (existing, replacement) ->
                                (existing.getInterviewDate().isAfter(replacement.getInterviewDate()) ? existing : replacement)
                ));

        // Map to the DTO
        return latestInterviewMap.entrySet().stream()
                .map(entry -> {
                    Employee emp = entry.getKey();
                    InterviewDetails recentInterview = entry.getValue();

                    return new EmployeeInterviewSummaryDTO(
                            emp.getEmpId(),
                            emp.getName(), // Assuming Employee entity has a getName() method
                            recentInterview.getTechnologyStack(),
                            recentInterview.getClientName(),
                            recentInterview.getInterviewDate(),
                            recentInterview.getResult(),
                            recentInterview.getFeedback()
                    );
                })
                .collect(Collectors.toList());
    }
    public List<InterviewDetails> getInterviewsByEmployeeId(int empId) {
        // Uses the custom repository method: sorted by date descending (most recent first)
        return interviewDetailsRepository.findByEmployeeEmpIdOrderByInterviewDateDesc(empId);
    }

    @Transactional
    public InterviewDetails addInterviewDetails(InterviewDetailsRequest request) {

        Employee employee = employeeRepository.findById((long) request.getEmpId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + request.getEmpId()));

        InterviewDetails newInterview = new InterviewDetails();

        newInterview.setEmployee(employee);
        newInterview.setInterviewDate(request.getInterviewDate());
        newInterview.setClientName(request.getClientName());
        newInterview.setTechnologyStack(request.getTechnologyStack());
        newInterview.setFeedback(request.getFeedback());
        newInterview.setResult(request.getResult());
       // newInterview.setCreatedTs(LocalDateTime.now());

        return interviewDetailsRepository.save(newInterview);
    }

    // 4. PUT Logic (Update Result/Feedback)
    @Transactional
    public InterviewDetails updateInterviewResult(int interviewId, UpdateResultRequest request) {
        InterviewDetails existingInterview = interviewDetailsRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("Interview record not found with ID: " + interviewId));

        // Requirement: Result and Feedback can be updated anytime
        if (request.getResult() != null) {
            existingInterview.setResult(request.getResult());
        }
        if (request.getFeedback() != null) {
            existingInterview.setFeedback(request.getFeedback());
        }

        // Optionally update a lastUpdatedTs audit field here if you add it to the entity

        return interviewDetailsRepository.save(existingInterview);
    }
}
