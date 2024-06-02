package com.receipt2recipe.r2r.api;

import com.receipt2recipe.r2r.domain.Ingredient;
import com.receipt2recipe.r2r.domain.Member;
import com.receipt2recipe.r2r.domain.RefAndIgdt;
import com.receipt2recipe.r2r.service.FridgeService;
import com.receipt2recipe.r2r.service.IngredientService;
import com.receipt2recipe.r2r.service.VisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class APIOCRController {

    private final VisionService visionService;
    private final IngredientService ingredientService;
    private final FridgeService fridgeService;

    @PostMapping("/ocr")
    public ResponseEntity<List<Map<String, Object>>> extractText(@RequestParam("image") MultipartFile image, HttpSession session) {
        List<Map<String, Object>> response = new ArrayList<>();
        try {
            Member member = (Member) session.getAttribute("user");

            Long fridgeId = member.getFridge().getRfId();

            String extractedText = visionService.extractTextFromImage(image);
            List<String> matchedIngredientNames = ingredientService.findMatchingIngredients(extractedText);
            List<Ingredient> matchedIngredients = matchedIngredientNames.stream()
                    .map(ingredientService::findByName)
                    .collect(Collectors.toList());
            List<Ingredient> fridgeIngredients = fridgeService.getMyIngredients(fridgeId)
                    .stream().map(RefAndIgdt::getIngredient).collect(Collectors.toList());

            response = matchedIngredients.stream().map(ingredient -> {
                Map<String, Object> ingredientMap = new HashMap<>();
                ingredientMap.put("igdtId", ingredient.getIgdtId());
                ingredientMap.put("igdtName", ingredient.getIgdtName());
                ingredientMap.put("isExist", fridgeIngredients.contains(ingredient));
                return ingredientMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to extract text");
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList(errorResponse));
        }
    }

    @PostMapping("/ocr_add")
    public ResponseEntity<Map<String, String>> addToFridge(@RequestParam("q") List<Long> igdtIds, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        Map<String, String> response = new HashMap<>();

        if (igdtIds == null || igdtIds.isEmpty()) {
            response.put("message", "No ingredients selected");
            return ResponseEntity.badRequest().body(response);
        }

        Long fridgeId = member.getFridge().getRfId();
        try {
            for (Long igdtId : igdtIds) {
                fridgeService.addIngredientToFridge(fridgeId, igdtId, LocalDate.now());
            }
            response.put("message", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
