package com.project.last_yumyum.page

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.last_yumyum.Dataclass.Ingredient
import com.project.last_yumyum.R
import com.project.last_yumyum.retrofitClient.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class PictureAdd : AppCompatActivity() {

    private val CAMERA_CODE = 98
    private val STORAGE_CODE = 99

    private lateinit var imageUri: Uri
    private lateinit var listView: ListView
    private var selectedIngredient: Ingredient? = null
    private lateinit var confirmButton: Button
    private var ingredients: List<Ingredient>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_picture_add)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.button_select_photo).setOnClickListener {
            checkPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_CODE)
        }

        findViewById<Button>(R.id.button_upload).setOnClickListener {
            // ImageView에서 비트맵 가져오기
            val imageView = findViewById<ImageView>(R.id.imageView)
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap

            // 이미지를 서버에 업로드
            uploadImageToServer(bitmap)
        }

        listView = findViewById(R.id.listView)
        confirmButton = findViewById(R.id.button_confirm)
        confirmButton.setOnClickListener {
            selectedIngredient?.let {
                addIngredients(it.igdtId)
            } ?: run {
                Toast.makeText(this, "재료를 선택하세요.", Toast.LENGTH_LONG).show()
            }
        }

        findViewById<Button>(R.id.button_take_photo).setOnClickListener {
            checkPermission(arrayOf(Manifest.permission.CAMERA), CAMERA_CODE)
        }
    }

    private fun checkPermission(permissions: Array<out String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, permissions, requestCode)
                    return
                }
            }
        }
        if (requestCode == CAMERA_CODE) {
            callCamera()
        } else if (requestCode == STORAGE_CODE) {
            selectImage()
        }
    }

    private fun callCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_CODE)
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, STORAGE_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            if (requestCode == CAMERA_CODE) {
                callCamera()
            } else if (requestCode == STORAGE_CODE) {
                selectImage()
            }
        } else {
            Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            findViewById<ImageView>(R.id.imageView).setImageBitmap(imageBitmap)
            uploadImageToServer(imageBitmap)
        } else if (requestCode == STORAGE_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                val imageBitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                findViewById<ImageView>(R.id.imageView).setImageBitmap(imageBitmap)
                uploadImageToServer(imageBitmap)
            }
        }
    }

    private fun uploadImageToServer(bitmap: Bitmap) {
        val file = bitmapToFile(bitmap)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val call = RetrofitClient.api.uploadImage(body)
        call.enqueue(object : Callback<List<Ingredient>> {
            override fun onResponse(
                call: Call<List<Ingredient>>,
                response: Response<List<Ingredient>>
            ) {
                if (response.isSuccessful) {
                    ingredients = response.body()
                    setupListView()
                    Log.d("Upload Success", "Ingredients: $ingredients")
                } else {
                    Log.e("Upload Error", "Upload failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Ingredient>>, t: Throwable) {
                Log.e("Upload Error", t.message.toString())
            }
        })
    }

    private fun setupListView() {
        val ingredientsCopy = ingredients // 로컬 변수로 복사
        if (ingredientsCopy != null) {
            val ingredientNames = ingredientsCopy.map { it.igdtName }
            val adapter =
                ArrayAdapter(this@PictureAdd, android.R.layout.simple_list_item_1, ingredientNames)
            listView.adapter = adapter
            listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            listView.setOnItemClickListener { parent, view, position, id ->
                selectedIngredient = ingredientsCopy[position]
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val filesDir = applicationContext.filesDir
        val imageFile = File(filesDir, "image.jpg")
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }
        return imageFile
    }

    private fun addIngredients(ingredientIds: Int) {
        RetrofitClient.api.addIngredients(ingredientIds).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PictureAdd, "재료 추가 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PictureAdd, "재료 추가 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PictureAdd, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

}