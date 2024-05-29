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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<Map<String, Object>> extractText(@RequestParam("image") MultipartFile image, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
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

            List<Map<String, Object>> ingredientsList = matchedIngredients.stream().map(ingredient -> {
                Map<String, Object> ingredientMap = new HashMap<>();
                ingredientMap.put("igdt_id", ingredient.getIgdtId());
                ingredientMap.put("igdt_name", ingredient.getIgdtName());
                ingredientMap.put("isExist", fridgeIngredients.contains(ingredient));
                return ingredientMap;
            }).collect(Collectors.toList());

            response.put("ingredients", ingredientsList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Failed to extract text");
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
