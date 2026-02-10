package com.bajaj;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class BfhlController {

    private final String EMAIL = "janvi1253.be23@chitkarauniversity.edu.in";
    
    // REPLACE WITH YOUR ACTUAL KEY
    private final String GEMINI_API_KEY = "AIzaSyATmsbZyV33wB-T+cRXNWctJYXK7i4Ja2nE";
    
    // Using the specific model version you requested
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + GEMINI_API_KEY;

    /**
     * GET /health
     * Requirement: Returns success and email
     */
    @GetMapping("/health")
    public ResponseEntity<BfhlResponse> healthCheck() {
        return ResponseEntity.ok(new BfhlResponse(true, EMAIL));
    }

    /**
     * POST /bfhl
     * Requirement: Handles exactly one key: fibonacci, prime, lcm, hcf, AI
     */
    @PostMapping("/bfhl")
    public ResponseEntity<BfhlResponse> handlePost(@RequestBody Map<String, Object> request) {
        try {
            // Robust Input Validation: Ensure exactly one key is present
            if (request == null || request.size() != 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BfhlResponse(false, EMAIL));
            }

            String key = request.keySet().iterator().next();
            Object value = request.get(key);

            switch (key) {
                case "fibonacci":
                    int n = Integer.parseInt(value.toString());
                    return ResponseEntity.ok(new BfhlResponse(true, EMAIL, generateFibonacci(n)));

                case "prime":
                    List<Integer> primeInput = (List<Integer>) value;
                    return ResponseEntity.ok(new BfhlResponse(true, EMAIL, filterPrimes(primeInput)));

                case "lcm":
                    List<Integer> lcmInput = (List<Integer>) value;
                    return ResponseEntity.ok(new BfhlResponse(true, EMAIL, calculateLCM(lcmInput)));

                case "hcf":
                    List<Integer> hcfInput = (List<Integer>) value;
                    return ResponseEntity.ok(new BfhlResponse(true, EMAIL, calculateHCF(hcfInput)));

                case "AI":
                    return ResponseEntity.ok(new BfhlResponse(true, EMAIL, askGemini(value.toString())));

                default:
                    // Requirement: Handle unknown keys
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new BfhlResponse(false, EMAIL));
            }
        } catch (Exception e) {
            // Graceful error handling: return false instead of crashing
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BfhlResponse(false, EMAIL));
        }
    }

    // --- AI Logic (Gemini 2.5 Flash Lite) ---
    private String askGemini(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // Setting system instruction to ensure a single-word response
            String specializedPrompt = "Answer the following question in exactly one single word only: " + prompt;

            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", specializedPrompt)
                    ))
                )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_URL, entity, Map.class);
            
            if (response.getBody() != null && response.getBody().containsKey("candidates")) {
                List candidates = (List) response.getBody().get("candidates");
                Map content = (Map) ((Map) candidates.get(0)).get("content");
                List parts = (List) content.get("parts");
                String result = (String) ((Map) parts.get(0)).get("text");
                return result.trim().replaceAll("[^a-zA-Z0-9]", ""); // Clean response
            }
            return "Unknown";
        } catch (Exception e) {
            return "AI_Unavailable"; 
        }
    }

    // --- Math Logic Helper Methods ---

    private List<Integer> generateFibonacci(int n) {
        List<Integer> series = new ArrayList<>();
        int a = 0, b = 1;
        for (int i = 0; i < n; i++) {
            series.add(a);
            int next = a + b;
            a = b;
            b = next;
        }
        return series;
    }

    private List<Integer> filterPrimes(List<Integer> nums) {
        List<Integer> primes = new ArrayList<>();
        for (Object o : nums) {
            int num = Integer.parseInt(o.toString());
            if (num < 2) continue;
            boolean isP = true;
            for (int i = 2; i <= Math.sqrt(num); i++) {
                if (num % i == 0) { isP = false; break; }
            }
            if (isP) primes.add(num);
        }
        return primes;
    }

    private int calculateHCF(List<Integer> nums) {
        int result = Integer.parseInt(nums.get(0).toString());
        for (int i = 1; i < nums.size(); i++) {
            result = gcd(result, Integer.parseInt(nums.get(i).toString()));
        }
        return result;
    }

    private int gcd(int a, int b) {
        while (b != 0) { a %= b; int temp = a; a = b; b = temp; }
        return a;
    }

    private int calculateLCM(List<Integer> nums) {
        int result = Integer.parseInt(nums.get(0).toString());
        for (int i = 1; i < nums.size(); i++) {
            int nextVal = Integer.parseInt(nums.get(i).toString());
            result = (result * nextVal) / gcd(result, nextVal);
        }
        return result;
    }
}