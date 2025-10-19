package com.moducrafter.appMod.serviceImpl;

import com.moducrafter.appMod.dto.ResumeParserResponse;
import com.moducrafter.appMod.service.AIUdemyService;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;
import org.xml.sax.SAXException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIUdemyServiceImpl implements AIUdemyService {


  @Value("${ai.api.key:${AI_API_KEY:}}")
  private String aiApiKey;

  @Value("${ai.api.url}")
  private String aiApiUrl;

  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();

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

    String fullUrl = aiApiUrl + "?key=" + aiApiKey;

    String userPrompt = "Interview Feedback: \"" + feedback + "\"";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> body = new HashMap<>();
    body.put("systemInstruction", Map.of(
      "parts", List.of(
        Map.of("text", SYSTEM_INSTRUCTION)
      )
    ));
    body.put("generationConfig", Map.of(
      "responseMimeType", "application/json"
    ));


    body.put("contents", List.of(
      Map.of("role", "user", "parts", List.of(
        Map.of("text", userPrompt)
      ))
    ));

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

    try {
      ResponseEntity<Map> response = restTemplate.exchange(
        fullUrl,
        HttpMethod.POST,
        entity,
        Map.class
      );

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

      String jsonOutput = parts.get(0).get("text").toString().trim();

      if (jsonOutput.startsWith("```json")) {
        jsonOutput = jsonOutput.substring(7, jsonOutput.lastIndexOf("```")).trim();
      }

      return jsonOutput;

    } catch (HttpClientErrorException e) {
      System.err.println("Gemini API HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
      return "{\"error\": \"API Request Failed: " + e.getStatusCode() + "\", \"details\": \"" + e.getResponseBodyAsString().replaceAll("\"", "'") + "\"}";
    } catch (ResourceAccessException e) {
      System.err.println("Network/Connection Error: " + e.getMessage());
      return "{\"error\": \"Network Error: Could not connect to  API.\"}";
    } catch (Exception e) {
      System.err.println("Unknown Error during API call or parsing: " + e.getMessage());
      e.printStackTrace();
      return "{\"error\": \"Internal processing error.\"}";
    }
  }

  @Override
  public List<String> extractSkills(String extractedText) {
    if (extractedText == null || extractedText.trim().isEmpty()) {
      return List.of();
    }

    try {
      // Define the Structured JSON Schema (as a Map)
      Map<String, Object> skillsSchema = Map.of(
        "type", "object",
        "properties", Map.of(
          "extractedSkills", Map.of(
            "type", "array",
            "items", Map.of("type", "string")
          )
        ),
        "required", List.of("extractedSkills")
      );

      String prompt = buildExtractionPrompt(extractedText);

      Map<String, Object> requestBody = Map.of(
        "contents", List.of(
          Map.of(
            "role", "user",
            "parts", List.of(
              Map.of("text", prompt)
            )
          )
        ),
        "generationConfig", Map.of(
          "responseMimeType", "application/json",
          "responseSchema", skillsSchema
        )
      );

      // Prepare Headers and Entity
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      String fullUrl = aiApiUrl + "?key=" + aiApiKey;
      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

      // 4. Execute the API Call
      ResponseEntity<Map> response = restTemplate.exchange(
        fullUrl,
        HttpMethod.POST,
        entity,
        Map.class
      );

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {

        // Navigate the nested response Map structure: candidates -> content -> parts -> text
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");

        if (candidates != null && !candidates.isEmpty()) {
          Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
          List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
          String jsonSkillsString = parts.get(0).get("text");


          ResumeParserResponse parserResponse =
            objectMapper.readValue(jsonSkillsString, ResumeParserResponse.class);

          return parserResponse.getExtractedSkills() != null ?
            parserResponse.getExtractedSkills() :
            List.of();
        }
      }

    } catch (Exception e) {
      System.err.println("Error calling Gemini API: " + e.getMessage());
      e.printStackTrace();
    }

    return List.of();
  }

  private String buildExtractionPrompt(String resumeText) {
    return """
      Analyze the following resume text. Your task is to **focus primarily on the 'Technologies' and 'Professional Overview' sections** to identify technical skills.

      Extract all technical skills, programming languages, frameworks, databases, and DevOps/Cloud tools (like Apache Kafka or GCP).

      Do not include non-technical terms like 'Mentorship', 'Payment', 'Risk', 'compliance', 'ERP', 'AC', or 'SCM' in the final list.

      Generate the output as a JSON object that strictly adheres to the provided schema.
      The array of skills must be under the key "extractedSkills".

      RESUME TEXT:
      ---
      """ + resumeText + "\n---";
  }

  @Override
  public Map<String, Object> extractCourseDetails(InputStream courseFileStream, String fileName) {

    AutoDetectParser parser = new AutoDetectParser();
    BodyContentHandler handler = new BodyContentHandler(-1);
    Metadata metadata = new Metadata();

    //  TikaInputStream for robust content type detection
    try (TikaInputStream tikaInputStream = TikaInputStream.get(courseFileStream)) {

      metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, fileName);

      parser.parse(tikaInputStream, handler, metadata);

      Map<String, Object> results = new HashMap<>();

      results.put("content", handler.toString());

      Map<String, String> metadataMap = new HashMap<>();
      for (String name : metadata.names()) {
        metadataMap.put(name, metadata.get(name));
      }
      results.put("metadata", metadataMap);

      return results;
    } catch (IOException | TikaException | SAXException e) {
      throw new RuntimeException(e);
    }
  }
}
