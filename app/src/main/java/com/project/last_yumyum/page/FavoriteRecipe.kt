package com.project.last_yumyum.page

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.last_yumyum.Dataclass.MainPage
import com.project.last_yumyum.R
import com.project.last_yumyum.retrofitClient.RetrofitClient


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteRecipe : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorite_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listView: ListView = findViewById(R.id.listView)
        fetchFavoriteRecipes(listView)

        val transitionRecommendButton1 = findViewById<Button>(R.id.back_button)
        transitionRecommendButton1.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

        val transitionRecommendButton = findViewById<Button>(R.id.deleteButton)
        transitionRecommendButton.setOnClickListener {
            val intent = Intent(this, DeleteFavoriteRecipe::class.java)
            startActivity(intent)
        }


    }

    private fun fetchFavoriteRecipes(listView: ListView) {
        RetrofitClient.api.getMyHearts().enqueue(object : Callback<List<MainPage>> {
            override fun onResponse(
                call: Call<List<MainPage>>,
                response: Response<List<MainPage>>
            ) {
                if (response.isSuccessful) {
                    val recipes = response.body()
                    if (recipes != null) {
                        val recipeNames = recipes.map { it.rcpName }
                        val recipeIds = recipes.map { it.rcpId }

                        listView.adapter = ArrayAdapter(
                            this@FavoriteRecipe,
                            android.R.layout.simple_list_item_1,
                            recipeNames
                        )

                        listView.setOnItemClickListener { parent, view, position, id ->
                            val selectedRecipeId = recipeIds[position]
                            val intent = Intent(this@FavoriteRecipe, recipe_detail::class.java)
                            intent.putExtra("RECIPE_ID", selectedRecipeId)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this@FavoriteRecipe, "찜 목록을 불러올 수 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this@FavoriteRecipe, "서버 응답이 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<MainPage>>, t: Throwable) {
                Toast.makeText(this@FavoriteRecipe, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}
