package com.project.last_yumyum.Dataclass

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


//로그인 요청
data class LogIn(
    val userEmail: String,
    val userPw: String
)

// 로그인  확인
data class LoginResponse(
    val success: Boolean,
    val message: String
)

// 회원가입 요청
data class Register(
    val userName: String,
    val userEmail: String,
    val userPw: String,
    val userPhone: String
)

// 회원가입 확인
data class SignUpResponse(
    val success: Boolean,
    val message: String? = null
)

// 홈페이지//
data class MainPage(
    val rcpId: Int,
    val rcpName: String,
    val rcpCookingTime: String,
    val rcpDifficulty: String,
    val rcpImageUrl: String,
    val rcpQuantities: String
)
data class RecipeDetail(
    val rcpName: String,
    val rcpCookingTime: String,
    val rcpDifficulty: String,
    val rcpImageUrl: String,
    val rcpQuantities: String,
    val rcpSteps: List<RecipeStep>,
    val rcpIgdt: List<RecipeIngredient>,
)
data class RecipeStep(
    val stepNumber: Int,
    val description: String,
    val stepUrl: String
)

// 레시피 재료
data class RecipeIngredient(
    val isExist: Boolean,
    val igdtName: String
)

//현재 가지고 있는 식재료를 끌어올 데이터 클래스
//나의 냉장고에 저장된 재료
data class ShowIngredient(
    val igdtId: Int,
    val name: String,
    val purchaseDate: String
)

data class Ingredient(
    val igdtName: String,
    val isExist: Boolean,
    val igdtId: Int
)


// 원하는 레시피 찾을떄 가져올 값들
data class SearchRecipe(
    val rcpName: String,
    val recipeImageURL: String
)

data class RecipeShow(
    val rcpId: Int, // 레시피 ID 필드 추가
    val recipeName: String,
    val ingredient: String,
    val cookingSequence: String,
    val recipePictureUrl: String
)

data class SaveList(
    val recipeId: Int, // 레시피 id
    val recipePictureUrl: String, // 불러올 사진
    val recipeName: String // 레시피 이름
)

data class FavoriteResponse(
    val success: Boolean,
    val message: String? = null
)


