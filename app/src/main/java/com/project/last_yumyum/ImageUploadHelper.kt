package com.project.last_yumyum

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.project.last_yumyum.Dataclass.Ingredient
import com.project.last_yumyum.retrofitClient.RetrofitClient

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

object ImageUploadHelper {

    fun uploadImage(context: Context, imageUri: Uri, imageBitmap: Bitmap?) {
        val imageFile = File(imageUri.path)
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        val byteStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream)
        val imageBytes: ByteArray = byteStream.toByteArray()

        val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        val requestBody: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), base64Image)

        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

        val retrofitClient = RetrofitClient.api

        retrofitClient.uploadImage(imagePart).enqueue(object : Callback<List<Ingredient>> {
            override fun onResponse(call: Call<List<Ingredient>>, response: Response<List<Ingredient>>) {
                // 서버로부터 응답을 받았을 때의 처리
                if (response.isSuccessful) {
                    val ingredients = response.body()
                    Log.d("Ingredientids12", ingredients.toString()) // 로그 확인


                    // 서버에서 받은 응답을 처리하는 코드 작성
                } else {
                    // 서버로부터 실패 응답을 받았을 때의 처리
                }
            }

            override fun onFailure(call: Call<List<Ingredient>>, t: Throwable) {
                // 통신 실패 시 처리
                Toast.makeText(context, "업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}