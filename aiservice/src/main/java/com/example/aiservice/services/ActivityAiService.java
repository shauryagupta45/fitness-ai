package com.example.aiservice.services;

import com.example.aiservice.models.Activity;
import com.example.aiservice.models.Recommendation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getRecommendations(prompt);
//        log.info("RESPONSE FROM AI {}",geminiService.getRecommendations(prompt));
        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates").get(0)
                    .path("content")
                    .get("parts")
                    .get(0)
                    .path("text");
            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();

            log.info("Refined JSON Response : {}",jsonContent);
            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");

            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis,analysisNode,"overall","Overall : ");
            addAnalysisSection(fullAnalysis,analysisNode,"pace","Pace : ");
            addAnalysisSection(fullAnalysis,analysisNode,"heartRate","Heart Rate : ");
            addAnalysisSection(fullAnalysis,analysisNode,"caloriesBurned","Calories Burned : ");
            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety = extractSafety(analysisJson.path("safety"));
            Recommendation response =  Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .build();
            log.info("Recommendation Response : {}",response);
            return response;
        }catch(Exception ex){
            ex.printStackTrace();
            Recommendation response =  createDefaultRecommendation(activity);
            return response;
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting fitness consultant"))
                .safety(Arrays.asList(
                        "Always warm up",
                        "Listen to your body, it tells you the truth"
                ))
                .build();
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {

        List<String> improvements = new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String details = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s",area,details));
            });
        }
        return  improvements.isEmpty() ? Collections.singletonList("No Recommendation for the activity")
                :improvements ;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {

        List<String> suggestions = new ArrayList<>();
        if(suggestionsNode.isArray()){
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s",workout,description));
            });
        }
        return  suggestions.isEmpty() ? Collections.singletonList("No suggestions for the user")
                :suggestions ;
    }

    private List<String> extractSafety(JsonNode safetyNode) {

        List<String> safetyList = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(safety -> {
                safetyList.add(safety.asText());
            });
        }
        return  safetyList.isEmpty() ? Collections.singletonList("Follow general safety guidelines.")
                :safetyList ;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {

        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
