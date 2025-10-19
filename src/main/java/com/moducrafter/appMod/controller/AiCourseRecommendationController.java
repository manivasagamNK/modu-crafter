package com.moducrafter.appMod.controller;

import com.moducrafter.appMod.service.AIUdemyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/recommendation")
public class AiCourseRecommendationController {

  @Autowired
  private AIUdemyService aiUdemyService;

  @PostMapping
  public ResponseEntity<String> getAiRecommendations(@RequestBody Map<String, String> request) {
    String feedback = request.get("feedback");
    String result = aiUdemyService.getRecommendations(feedback);
    return ResponseEntity.ok(result);
  }
}
