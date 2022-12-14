package com.dicoding.picodiploma.loginwithanimation.view.detailstory

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoryBinding
import com.dicoding.picodiploma.loginwithanimation.getTimeUpload
import com.dicoding.picodiploma.loginwithanimation.loadImage
import com.dicoding.picodiploma.loginwithanimation.local.LocalStories

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
        setupView()
    }

    private fun getData(){
        binding.apply {
            val story = intent?.extras?.getParcelable<LocalStories>(DATA_STORY)

            Log.d("Data Detail Activity", story.toString())
            tvUserDetail.text = story?.name
            tvDateDetail.text = story?.createdAt?.let { getTimeUpload(it) }
            tvDescription.text = story?.description
            story?.photoUrl?.let { ivCoverDetail.loadImage(it) }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        const val DATA_STORY = "data_story"
    }
}