package com.project.last_yumyum.page

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
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

class SearchRecipe : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchButton: Button = findViewById(R.id.search_button)
        val searchEditText: EditText = findViewById(R.id.searchEditText)
        val listView: ListView = findViewById(R.id.listViewRecipes)
        setupBottomNavigation()
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            RetrofitClient.api.searchRecipe(query).enqueue(object : Callback<List<MainPage>> {
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
                                this@SearchRecipe,
                                android.R.layout.simple_list_item_1,
                                recipeNames
                            )

                            listView.setOnItemClickListener { parent, view, position, id ->
                                val selectedRecipeId = recipeIds[position]
                                val intent = Intent(this@SearchRecipe, recipe_detail::class.java)
                                intent.putExtra("RECIPE_ID", selectedRecipeId)
                                startActivity(intent)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<MainPage>>, t: Throwable) {
                    // 오류 처리
                    Toast.makeText(this@SearchRecipe, "검색 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    ////////////////////////하단바 페이지변경
    private fun setupBottomNavigation() {
        val transitionHomeButton = findViewById<Button>(R.id.home)
        transitionHomeButton.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
        val transitionListItemButton = findViewById<Button>(R.id.list_item)
        transitionListItemButton.setOnClickListener {
            val intent = Intent(this, RetainedIngredient::class.java)
            startActivity(intent)
        }
        val transitionPictureAddButton = findViewById<Button>(R.id.picture_add)
        transitionPictureAddButton.setOnClickListener {
            val intent = Intent(this, PictureAdd::class.java)
            startActivity(intent)
        }

        val transitionSearchButton = findViewById<Button>(R.id.search)
        transitionSearchButton.setOnClickListener {
            val intent = Intent(this, SearchRecipe::class.java)
            startActivity(intent)
        }

        val transitionRecommendButton = findViewById<Button>(R.id.recommend)
        transitionRecommendButton.setOnClickListener {
            val intent = Intent(this, Recommend::class.java)
            startActivity(intent)
        }


    }
}
