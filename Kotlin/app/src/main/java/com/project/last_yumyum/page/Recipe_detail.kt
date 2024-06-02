package com.project.last_yumyum.page

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.last_yumyum.Adapter.RecipeStepAdapter
import com.project.last_yumyum.Dataclass.RecipeDetail
import com.project.last_yumyum.R
import com.project.last_yumyum.retrofitClient.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class recipe_detail : AppCompatActivity() {

    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var stepsAdapter: RecipeStepAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<Button>(R.id.button_back)
        backButton.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

        val recipeId = intent.getIntExtra("RECIPE_ID", -1)
        if (recipeId != -1) {
            fetchRecipeDetails(recipeId)
        } else {
            Toast.makeText(this, "레시피 ID가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }

        val favoriteButton = findViewById<Button>(R.id.button2)
        favoriteButton.setOnClickListener {
            if (recipeId != -1) {
                addRecipeToFavorites(recipeId)
            } else {
                Toast.makeText(this, "레시피를 추가할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        stepsRecyclerView = findViewById(R.id.stepsRecyclerView)
        stepsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchRecipeDetails(recipeId: Int) {
        RetrofitClient.api.getRecipeDetailById(recipeId).enqueue(object : Callback<RecipeDetail> {
            override fun onResponse(call: Call<RecipeDetail>, response: Response<RecipeDetail>) {
                if (response.isSuccessful) {
                    val recipe = response.body()
                    if (recipe != null) {
                        updateUI(recipe)
                    } else {
                        Toast.makeText(this@recipe_detail, "레시피를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@recipe_detail, "서버 응답이 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipeDetail>, t: Throwable) {
                Toast.makeText(this@recipe_detail, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(recipe: RecipeDetail) {
        val recipeImageView = findViewById<ImageView>(R.id.recipeImageView)
        val recipeNameTextView = findViewById<TextView>(R.id.textView)
        val cookingTimeTextView = findViewById<TextView>(R.id.cookingTimeTextView)
        val difficultyTextView = findViewById<TextView>(R.id.difficultyTextView)
        val quantitiesTextView = findViewById<TextView>(R.id.quantitiesTextView)
        val ingredientsTextView = findViewById<TextView>(R.id.ingredientsTextView)

        Glide.with(this).load(recipe.rcpImageUrl).into(recipeImageView)
        recipeNameTextView.text = recipe.rcpName
        cookingTimeTextView.text = "Cooking Time: ${recipe.rcpCookingTime}"
        difficultyTextView.text = "Difficulty: ${recipe.rcpDifficulty}"
        quantitiesTextView.text = "Servings: ${recipe.rcpQuantities}"
        ingredientsTextView.text = "Ingredients:\n" + recipe.rcpIgdt.joinToString("\n") { "- ${it.igdtName} ${if (it.isExist) "(O)" else "(X)"}" }

        stepsAdapter = RecipeStepAdapter(recipe.rcpSteps)
        stepsRecyclerView.adapter = stepsAdapter
    }

    private fun addRecipeToFavorites(recipeId: Int) {
        RetrofitClient.api.addFavoriteRecipe(recipeId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@recipe_detail, "레시피가 찜 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@recipe_detail, "찜 목록 추가 실패: 서버 오류", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@recipe_detail, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
