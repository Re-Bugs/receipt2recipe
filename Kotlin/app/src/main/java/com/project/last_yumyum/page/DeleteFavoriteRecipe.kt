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

class DeleteFavoriteRecipe : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var deleteButton: Button
    private lateinit var selectedRecipe: MainPage
    private lateinit var adapter: ArrayAdapter<String>
    private var recipes: List<MainPage> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delete_favorite_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.listView)
        deleteButton = findViewById(R.id.deleteButton)

        fetchFavoriteRecipes()

        deleteButton.setOnClickListener {
            if (::selectedRecipe.isInitialized) {
                deleteFavoriteRecipe(selectedRecipe)
            } else {
                Toast.makeText(this, "삭제할 레시피를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedRecipe = recipes[position]
            Toast.makeText(this, "${selectedRecipe.rcpName} 선택됨", Toast.LENGTH_SHORT).show()
        }

        val transitionRecommendButton = findViewById<Button>(R.id.backbutton)
        transitionRecommendButton.setOnClickListener {
            val intent = Intent(this, FavoriteRecipe::class.java)
            startActivity(intent)
        }

    }

    private fun fetchFavoriteRecipes() {
        RetrofitClient.api.getMyHearts().enqueue(object : Callback<List<MainPage>> {
            override fun onResponse(
                call: Call<List<MainPage>>,
                response: Response<List<MainPage>>
            ) {
                if (response.isSuccessful) {
                    recipes = response.body() ?: listOf()
                    val recipeNames = recipes.map { it.rcpName }
                    adapter = ArrayAdapter(
                        this@DeleteFavoriteRecipe,
                        android.R.layout.simple_list_item_1,
                        recipeNames
                    )
                    listView.adapter = adapter
                } else {
                    Toast.makeText(this@DeleteFavoriteRecipe, "서버 응답이 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<List<MainPage>>, t: Throwable) {
                Toast.makeText(
                    this@DeleteFavoriteRecipe,
                    "네트워크 오류: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun deleteFavoriteRecipe(recipe: MainPage) {
        RetrofitClient.api.deleteFavoriteRecipe(recipe.rcpId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    recipes = recipes.filterNot { it.rcpId == recipe.rcpId }
                    val recipeNames = recipes.map { it.rcpName }
                    adapter.clear()
                    adapter.addAll(recipeNames)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this@DeleteFavoriteRecipe, "레시피가 삭제되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@DeleteFavoriteRecipe, "삭제 실패: 서버 오류", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@DeleteFavoriteRecipe,
                    "네트워크 오류: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
