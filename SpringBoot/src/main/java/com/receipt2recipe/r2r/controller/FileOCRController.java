package com.receipt2recipe.r2r.controller;

import com.receipt2recipe.r2r.domain.Ingredient;
import com.receipt2recipe.r2r.domain.Member;
import com.receipt2recipe.r2r.domain.RefAndIgdt;
import com.receipt2recipe.r2r.service.FileVisionService;
import com.receipt2recipe.r2r.service.FridgeService;
import com.receipt2recipe.r2r.service.IngredientService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/receipt")
public class FileOCRController {
    private final FileVisionService visionService;
    private final IngredientService ingredientService;
    private final FridgeService fridgeService;

    @GetMapping("/receipt_recognition")
    public String showUploadForm(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member != null) {
            model.addAttribute("userEmail", member.getUserEmail());
            model.addAttribute("userName", member.getUserName());
        } else {
            return "redirect:/sign_in";
        }
        return "receipt/receipt_recognition";
    }

    @PostMapping("/receipt_recognition")
    public String uploadFile(@RequestParam("receipt") MultipartFile file, RedirectAttributes redirectAttributes, HttpSession session) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "업로드할 파일을 선택해주세요.");
            return "redirect:/receipt/receipt_recognition";
        }

        try {
            Member member = (Member) session.getAttribute("user");
            if (member == null) {
                return "redirect:/sign_in";
            }
            Long fridgeId = member.getFridge().getRfId();

            String recipeText = visionService.extractTextFromFile(file); // 서비스로부터 인식한 텍스트를 받아옴
            List<String> matchedIngredients = ingredientService.findMatchingIngredients(recipeText);
            List<String> fridgeIngredients = fridgeService.getMyIngredients(fridgeId)
                    .stream().map(ri -> ri.getIngredient().getIgdtName()).collect(Collectors.toList());

            redirectAttributes.addFlashAttribute("message", "영수증 인식에 성공하였습니다.");
            redirectAttributes.addFlashAttribute("matchedIngredients", matchedIngredients);
            redirectAttributes.addFlashAttribute("fridgeIngredients", fridgeIngredients);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "영수증 인식에 실패하였습니다.");
            log.error(e.getMessage(), e);
        }

        return "redirect:/receipt/result";
    }

    @PostMapping("/add_to_fridge")
    public String addToFridge(@RequestParam(value = "ingredients", required = false) List<String> ingredientNames, HttpSession session, RedirectAttributes redirectAttributes) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return "redirect:/sign_in";
        }

        if (ingredientNames == null || ingredientNames.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "재료를 선택하지 않았습니다.");
            return "redirect:/receipt/result";
        }

        Long fridgeId = member.getFridge().getRfId();
        try {
            for (String ingredientName : ingredientNames) {
                Ingredient ingredient = ingredientService.findByName(ingredientName);
                fridgeService.addIngredientToFridge(fridgeId, ingredient.getIgdtId(), LocalDate.now());
            }
            redirectAttributes.addFlashAttribute("message", "영수증에서 인식된 재료가 냉장고에 성공적으로 추가되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "재료를 추가하는 동안 오류가 발생했습니다.");
            log.error(e.getMessage(), e);
        }
        return "redirect:/my_fridge";
    }


    @GetMapping("/result")
    public String showResultPage(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member != null) {
            model.addAttribute("userEmail", member.getUserEmail());
            model.addAttribute("userName", member.getUserName());
        } else {
            return "redirect:/sign_in";
        }
        return "receipt/result";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "사진의 크기가 너무 큽니다.");
        return "redirect:/receipt/receipt_recognition";
    }
}
