package com.project.last_yumyum.page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.project.last_yumyum.Dataclass.MainPage
import com.project.last_yumyum.Dataclass.ShowIngredient
import com.project.last_yumyum.R
import com.project.last_yumyum.retrofitClient.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetainedIngredient : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_retained_ingredient)
        setupBottomNavigation()

        val listView: ListView = findViewById(R.id.listView)
        fetchFavoriteRecipes(listView)

        val transitionRecommendButton = findViewById<Button>(R.id.deleteButton)
        transitionRecommendButton.setOnClickListener {
            val intent = Intent(this, DeleteIngredient::class.java)
            startActivity(intent)
        }
    }

    private fun fetchFavoriteRecipes(listView: ListView) {
        RetrofitClient.api.getAvailableIngredient()
            .enqueue(object : Callback<List<ShowIngredient>> {
                override fun onResponse(
                    call: Call<List<ShowIngredient>>,
                    response: Response<List<ShowIngredient>>
                ) {
                    if (response.isSuccessful) {
                        val ingredients = response.body()
                        if (ingredients != null && ingredients.isNotEmpty()) {
                            val ingredientNames = ingredients.map { it.name }
                            val ingredientids = ingredients.map { it.igdtId }
                            Log.d("IngredientNames", ingredients.toString()) // 로그 확인
                            Log.d("Ingredientids", ingredientids.toString()) // 로그 확인
                            listView.adapter = ArrayAdapter(
                                this@RetainedIngredient,
                                android.R.layout.simple_list_item_1,
                                ingredientNames
                            )
                        } else {
                            Toast.makeText(
                                this@RetainedIngredient,
                                "찜 목록이 없습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@RetainedIngredient,
                            "서버 응답이 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<ShowIngredient>>, t: Throwable) {
                    Toast.makeText(
                        this@RetainedIngredient,
                        "네트워크 오류: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun setupBottomNavigation() {
        val transitionHomeButton = findViewById<Button>(R.id.home)
        transitionHomeButton.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

        // 현재 액티비티이므로 클릭 시 아무 동작하지 않습니다.

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
