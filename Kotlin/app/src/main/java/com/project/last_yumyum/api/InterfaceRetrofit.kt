package com.project.last_yumyum.api

import com.project.last_yumyum.Dataclass.Ingredient
import com.project.last_yumyum.Dataclass.LogIn
import com.project.last_yumyum.Dataclass.LoginResponse
import com.project.last_yumyum.Dataclass.MainPage

import com.project.last_yumyum.Dataclass.RecipeDetail
import com.project.last_yumyum.Dataclass.Register
import com.project.last_yumyum.Dataclass.ShowIngredient
import com.project.last_yumyum.Dataclass.SignUpResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface InterfaceRetrofit {


    // 로그인
    @POST("/api/login/sign_in")
    fun login(@Body loginData: LogIn): Call<LoginResponse>

    // 회원가입 요청
    @POST("/api/login/sign_up")
    fun register(@Body registerData: Register): Call<SignUpResponse>

    // 일간 인기 레시피
    // 홈페이지
    @GET("/api/all_rcplist")
    fun getDailyRecipes(): Call<List<MainPage>>

    //상위 5개의 레시피 출력
    //홈페이지
    @GET("/api/5_rcplist")
    fun getTopFiveRecipes(): Call<List<MainPage>>

    // 현재 보유하고 있는 식재료
    @GET("/api/my_igdts")
    fun getAvailableIngredient(): Call<List<ShowIngredient>>

    // 식재료 추가
    @POST("/api/add_ingredient")
    fun addIngredient(@Body ingredientIds: List<String>): Call<Void>

    // 식재료 삭제
    @POST("/api/delete_ingredients")
    fun deleteIngredients(@Query("q") rcpId: Int): Call<Void>

    // 레시피 검색
    @GET("/api/search_recipe")
    fun searchRecipe(@Query("q") query: String): Call<List<MainPage>>


    @GET("/api/recommend_recipes")
    fun recommendRecipes(): Call<List<MainPage>>

    @GET("api/my_hearts")
    fun getMyHearts(): Call<List<MainPage>>


    // 기존 uploadImage 메서드
    @Multipart
    @POST("/api/ocr")
    fun uploadImage(@Part image: MultipartBody.Part): Call<List<Ingredient>>

    // 새로운 ocr_add 메서드 추가
    @POST("/api/ocr_add")
    fun addIngredients(@Query("q") ingredientIds: Int): Call<Void>

    // 찜 목록에서 레시피 삭제
    @POST("/api/delete_heart")
    fun deleteFavoriteRecipe(@Query("q") rcpId: Int): Call<Void>

    // 찜 목록에 레시피 추가
    @POST("/api/add_heart")
    fun addFavoriteRecipe(@Query("q") rcpId: Int): Call<Void>


    @GET("api/detail/{recipeId}")
    fun getRecipeDetailById(@Path("recipeId") recipeId: Int): Call<RecipeDetail>

}