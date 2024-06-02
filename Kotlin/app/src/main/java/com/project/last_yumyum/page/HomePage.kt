package com.project.last_yumyum.page

import TopFiveAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.last_yumyum.Adapter.RVAdapter
import com.project.last_yumyum.Dataclass.MainPage
import com.project.last_yumyum.R
import com.project.last_yumyum.retrofitClient.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePage : AppCompatActivity() {
    private val items = mutableListOf<MainPage>()
    private val topFiveItems = mutableListOf<MainPage>()

    private lateinit var rvAdapter: RVAdapter
    private lateinit var topFiveAdapter: TopFiveAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvAdapter = RVAdapter(this, items)
        topFiveAdapter = TopFiveAdapter(this, topFiveItems)

        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        recyclerView.adapter = rvAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 3) // 3개씩 출력

        val topFiveRecyclerView = findViewById<RecyclerView>(R.id.topFive)
        topFiveRecyclerView.adapter = topFiveAdapter
        topFiveRecyclerView.layoutManager = GridLayoutManager(this, 5)

        // 기본적으로 일간 레시피 로드
        fetchRecipes(RetrofitClient.api.getDailyRecipes())

        // Top 5 레시피 로드
        fetchTopFiveRecipes()

        // 하단바 페이지 변경
        setupBottomNavigation()

        val transitionHomeButton = findViewById<Button>(R.id.favoriteButton)
        transitionHomeButton.setOnClickListener {
            val intent = Intent(this, FavoriteRecipe::class.java)
            startActivity(intent)
        }
    }

    private fun fetchRecipes(call: Call<List<MainPage>>) {
        call.enqueue(object : Callback<List<MainPage>> {
            override fun onResponse(call: Call<List<MainPage>>, response: Response<List<MainPage>>) {
                if (response.isSuccessful) {
                    val recipes = response.body()
                    if (recipes != null) {
                        // 데이터를 리스트에 추가
                        items.clear()
                        items.addAll(recipes)
                        // 액티비티가 유효한 상태일 때만 UI 업데이트
                        if (!isDestroyed && !isFinishing) {
                            rvAdapter.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(this@HomePage, "응답 데이터가 비어 있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@HomePage, "서버 응답이 실패했습니다. 코드: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", response.errorBody()?.string() ?: "Error body is null")
                }
            }

            override fun onFailure(call: Call<List<MainPage>>, t: Throwable) {
                Log.e("MainActivity", "API call failed: ${t.message}")
                Toast.makeText(this@HomePage, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchTopFiveRecipes() {
        RetrofitClient.api.getTopFiveRecipes().enqueue(object : Callback<List<MainPage>> {
            override fun onResponse(call: Call<List<MainPage>>, response: Response<List<MainPage>>) {
                if (response.isSuccessful) {
                    val topFiveRecipes = response.body()
                    if (topFiveRecipes != null) {
                        // 데이터를 리스트에 추가
                        topFiveItems.clear()
                        topFiveItems.addAll(topFiveRecipes)
                        // 액티비티가 유효한 상태일 때만 UI 업데이트
                        if (!isDestroyed && !isFinishing) {
                            topFiveAdapter.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(this@HomePage, "응답 데이터가 비어 있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@HomePage, "서버 응답이 실패했습니다. 코드: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", response.errorBody()?.string() ?: "Error body is null")
                }
            }

            override fun onFailure(call: Call<List<MainPage>>, t: Throwable) {
                Log.e("MainActivity", "API call failed: ${t.message}")
                Toast.makeText(this@HomePage, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
