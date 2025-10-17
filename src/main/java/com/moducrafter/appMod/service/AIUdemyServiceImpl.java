package com.moducrafter.appMod.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIUdemyServiceImpl implements AIUdemyService {

  // Use property for the Gemini API Key
  // Recommended practice is to use GOOGLE_API_KEY environment variable
  @Value("${ai.api.key:${AI_API_KEY:}}")
  private String aiApiKey;

  @Value("${ai.api.url}")
  private String aiApiUrl;

  // Use the RestTemplate initialized once
  private final RestTemplate restTemplate = new RestTemplate();

  // Define the System Instruction for JSON output
  private static final String SYSTEM_INSTRUCTION =
    "You are an expert AI that recommends the top 3 Udemy courses based on skill gaps identified in the interview feedback. " +
      "You MUST return your entire response as a single, valid JSON object following this strict schema: " +
      "{\"recommendations\": [{\"title\": \"Course Title 1\", \"skill_gap\": \"Skill Gap 1\", \"link\": \"https://udemy.link.1\"}, " +
      "{\"title\": \"Course Title 2\", \"skill_gap\": \"Skill Gap 2\", \"link\": \"https://udemy.link.2\"}]}. " +
      "Ensure all fields are populated with relevant data.";

  @Override
  public String getRecommendations(String feedback) {
    if (aiApiKey == null || aiApiKey.trim().isEmpty()) {
      System.err.println("Gemini API Key is not configured.");
      return "{\"error\": \"API Key not configured.\"}";
    }

    // --- 1. Construct the Final URL with API Key ---
    // For the Generative Language API, the key is passed as a query parameter
    String fullUrl = aiApiUrl + "?key=" + aiApiKey;

    String userPrompt = "Interview Feedback: \"" + feedback + "\"";

    // --- 2. Build HTTP Headers ---
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // --- 3. Build the Request Body (Gemini API Structure) ---
    Map<String, Object> body = new HashMap<>();
    body.put("systemInstruction", Map.of(
      "parts", List.of(
        Map.of("text", SYSTEM_INSTRUCTION)
      )
    ));
    body.put("generationConfig", Map.of(
      "responseMimeType", "application/json"
      // You can also add "temperature", 0.1, etc. here
    ));


    // The user's prompt content
    body.put("contents", List.of(
      Map.of("role", "user", "parts", List.of(
        Map.of("text", userPrompt)
      ))
    ));

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

    try {
      // --- 4. Execute the API Call ---
      ResponseEntity<Map> response = restTemplate.exchange(
        fullUrl,
        HttpMethod.POST,
        entity,
        Map.class
      );

      // --- 5. Robust Parsing of Gemini Response ---
      Map<String, Object> responseBody = response.getBody();

      List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");

      if (candidates == null || candidates.isEmpty()) {
        // Check if an error message exists (e.g., Blocked reason)
        if (responseBody.containsKey("promptFeedback")) {
          return "{\"error\": \"Prompt Blocked by Safety Filters.\"}";
        }
        return "{\"error\": \"No content candidates returned from Gemini.\"}";
      }

      Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
      List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

      // The JSON output is the text content of the first part
      String jsonOutput = parts.get(0).get("text").toString().trim();

      // The API sometimes wraps the JSON in ```json...```, this removes it.
      if (jsonOutput.startsWith("```json")) {
        jsonOutput = jsonOutput.substring(7, jsonOutput.lastIndexOf("```")).trim();
      }

      return jsonOutput;

    } catch (HttpClientErrorException e) {
      System.err.println("Gemini API HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
      return "{\"error\": \"API Request Failed: " + e.getStatusCode() + "\", \"details\": \"" + e.getResponseBodyAsString().replaceAll("\"", "'") + "\"}";
    } catch (ResourceAccessException e) {
      System.err.println("Network/Connection Error: " + e.getMessage());
      return "{\"error\": \"Network Error: Could not connect to Gemini API.\"}";
    } catch (Exception e) {
      System.err.println("Unknown Error during API call or parsing: " + e.getMessage());
      e.printStackTrace();
      return "{\"error\": \"Internal processing error.\"}";
    }
  }
}
