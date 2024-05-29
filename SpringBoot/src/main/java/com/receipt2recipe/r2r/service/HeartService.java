package com.receipt2recipe.r2r.service;

import com.receipt2recipe.r2r.domain.Heart;
import com.receipt2recipe.r2r.domain.Member;
import com.receipt2recipe.r2r.domain.Recipe;
import com.receipt2recipe.r2r.dto.HeartDTO;
import com.receipt2recipe.r2r.repository.HeartRepository;
import com.receipt2recipe.r2r.repository.MemberRepository;
import com.receipt2recipe.r2r.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;
    private final RecipeRepository recipeRepository;

    public List<Heart> getUserFavorites(String userEmail) {
        return heartRepository.findByMemberUserEmail(userEmail);
    }

    public void addFavoriteRecipe(String userEmail, Long recipeId) {
        Optional<Heart> existingHeart = heartRepository.findByMemberUserEmailAndRecipeId(userEmail, recipeId);
        if (existingHeart.isPresent()) {
            throw new IllegalArgumentException("이미 찜한 레시피입니다.");
        }

        Member member = memberRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레시피입니다."));

        Heart heart = new Heart();
        heart.setMember(member);
        heart.setRecipe(recipe);

        heartRepository.save(heart);
    }

    public void removeFavoriteRecipe(String userEmail, Long recipeId) {
        Heart heart = heartRepository.findByMemberUserEmailAndRecipeId(userEmail, recipeId)
                .orElseThrow(() -> new IllegalArgumentException("찜한 레시피를 찾을 수 없습니다."));
        heartRepository.delete(heart);
    }

    public List<String> getFridgeIngredientsByUserEmail(String userEmail) {
        Member member = memberRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));

        return member.getFridge().getIngredients().stream()
                .map(refAndIgdt -> refAndIgdt.getIngredient().getIgdtName())
                .collect(Collectors.toList());
    }

    public List<HeartDTO> getHeartsByUserEmail(String userEmail) {
        List<Heart> hearts = heartRepository.findByMemberUserEmail(userEmail);
        return hearts.stream()
                .map(heart -> new HeartDTO(
                        heart.getRecipe().getId(),
                        heart.getRecipe().getName(),
                        heart.getRecipe().getCookingTime(),
                        heart.getRecipe().getDifficulty(),
                        heart.getRecipe().getImageUrl(),
                        heart.getRecipe().getQuantities()
                ))
                .collect(Collectors.toList());
    }
}
