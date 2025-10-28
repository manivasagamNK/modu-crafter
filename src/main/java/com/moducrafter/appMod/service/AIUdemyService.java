package com.moducrafter.appMod.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface AIUdemyService {
   String getRecommendations(String feedback);
  List<String> extractSkills(String extractedText);
  String getRelevantText(String fullResumeText);
  Map<String, Object> extractCourseDetails(InputStream courseFileStream, String fileName);
}
