package com.moducrafter.appMod.serviceImpl;

import com.moducrafter.appMod.dto.EmployeeInterviewSummaryDTO;
import com.moducrafter.appMod.dto.InterviewDetailsRequest;
import com.moducrafter.appMod.dto.UpdateResultRequest;
import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.model.InterviewDetails;
import com.moducrafter.appMod.repository.EmployeeRepository;
import com.moducrafter.appMod.repository.InterviewDetailsReposiorty;
import com.moducrafter.appMod.service.InterviewDetailsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InterviewDetailsServiceImpl implements InterviewDetailsService {
    @Autowired
    private InterviewDetailsReposiorty interviewDetailsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

  @Transactional
  @Override
  public InterviewDetails saveNewInterview(InterviewDetails interviewDetails) {
    return interviewDetailsRepository.save(interviewDetails);
  }


    @Override
    public List<EmployeeInterviewSummaryDTO> getEmployeeInterviewSummary() {
      // 1. Fetch all interview details to build the map
      List<InterviewDetails> allInterviews = interviewDetailsRepository.findAll();

      Map<Employee, InterviewDetails> latestInterviewMap = allInterviews.stream()
        .filter(details -> details.getInterviewDate() != null) // Essential to avoid NPE
        .collect(Collectors.toMap(
          InterviewDetails::getEmployee,
          interview -> interview,
          (existing, replacement) ->
            (existing.getInterviewDate().isAfter(replacement.getInterviewDate()) ? existing : replacement)
        ));

      List<Employee> allEmployees = employeeRepository.findAll();

      return allEmployees.stream()
        .map(employee -> {
          InterviewDetails recentInterview = latestInterviewMap.get(employee);

          String technologyStack = (recentInterview != null) ? recentInterview.getTechnologyStack() : null;
          String clientName = (recentInterview != null) ? recentInterview.getClientName() : null;
          LocalDate interviewDate = (recentInterview != null) ? recentInterview.getInterviewDate() : null;
          String result = (recentInterview != null) ? recentInterview.getResult() : null;
          String feedback = (recentInterview != null) ? recentInterview.getFeedback() : null;

          return new EmployeeInterviewSummaryDTO(
            employee.getEmpId(),
            employee.getName(),
            technologyStack,
            clientName,
            interviewDate,
            result,
            feedback
          );
        })
        .collect(Collectors.toList());
    }
    public List<InterviewDetails> getInterviewsByEmployeeId(int empId) {
        return interviewDetailsRepository.findByEmployeeEmpIdOrderByInterviewDateDesc(empId);
    }

    @Transactional
    public InterviewDetails addInterviewDetails(InterviewDetailsRequest request) {

        Employee employee = employeeRepository.findById(request.getEmpId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + request.getEmpId()));

        InterviewDetails newInterview = new InterviewDetails();

        newInterview.setEmployee(employee);
        newInterview.setInterviewDate(request.getInterviewDate());
        newInterview.setClientName(request.getClientName());
        newInterview.setTechnologyStack(request.getTechnologyStack());
        newInterview.setFeedback(request.getFeedback());
        newInterview.setResult(request.getResult());
        //newInterview.setCreatedTs(LocalDateTime.now());

        return interviewDetailsRepository.save(newInterview);
    }

    // 4. PUT Logic (Update Result/Feedback)
    @Transactional
    public InterviewDetails updateInterviewResult(int interviewId, UpdateResultRequest request) {
        InterviewDetails existingInterview = interviewDetailsRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("Interview record not found with ID: " + interviewId));

        if (request.getResult() != null) {
            existingInterview.setResult(request.getResult());
        }
        if (request.getFeedback() != null) {
            existingInterview.setFeedback(request.getFeedback());
        }
        if(request.getInterviewDate()!=null){
          existingInterview.setInterviewDate(request.getInterviewDate());
        }


        return interviewDetailsRepository.save(existingInterview);
    }
}
