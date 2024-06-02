package com.project.last_yumyum.page

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.last_yumyum.Dataclass.ShowIngredient
import com.project.last_yumyum.R
import com.project.last_yumyum.retrofitClient.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteIngredient : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var deleteButton: Button
    private var selectedIngredient: ShowIngredient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_ingredient)

        listView = findViewById(R.id.listView)
        deleteButton = findViewById(R.id.deleteButton)

        setupListView()

        deleteButton.setOnClickListener {
            selectedIngredient?.let {
                deleteIngredients(it.igdtId)
            } ?: run {
                Toast.makeText(this, "재료를 선택하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        val transitionRecommendButton = findViewById<Button>(R.id.backButton1)
        transitionRecommendButton.setOnClickListener {
            val intent = Intent(this, RetainedIngredient::class.java)
            startActivity(intent)
        }
    }

    private fun setupListView() {
        fetchIngredients { ingredients ->
            val ingredientNames = ingredients.map { it.name }
            listView.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_single_choice,
                ingredientNames
            )
            listView.choiceMode = ListView.CHOICE_MODE_SINGLE
            listView.setOnItemClickListener { parent, view, position, id ->
                selectedIngredient = ingredients[position]
            }
        }
    }

    private fun fetchIngredients(callback: (List<ShowIngredient>) -> Unit) {
        RetrofitClient.api.getAvailableIngredient()
            .enqueue(object : Callback<List<ShowIngredient>> {
                override fun onResponse(
                    call: Call<List<ShowIngredient>>,
                    response: Response<List<ShowIngredient>>
                ) {
                    if (response.isSuccessful) {
                        val ingredients = response.body() ?: emptyList()
                        callback(ingredients)
                    } else {
                        Toast.makeText(this@DeleteIngredient, "서버 응답 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<ShowIngredient>>, t: Throwable) {
                    Toast.makeText(this@DeleteIngredient, "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun deleteIngredients(ingredientIds: Int) {
        RetrofitClient.api.deleteIngredients(ingredientIds).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DeleteIngredient, "재료 삭제 성공", Toast.LENGTH_SHORT).show()
                    setupListView()
                } else {
                    Toast.makeText(this@DeleteIngredient, "재료 삭제 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@DeleteIngredient, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
