package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.repository.Repository
import com.dicoding.picodiploma.loginwithanimation.utility.LoadingDialog
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.add_story.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryPagingAdapter
    private lateinit var repository: Repository

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()

        lifecycleScope.launchWhenCreated {
            val adapter = StoryPagingAdapter()
            binding.apply {
                rvStories.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )
                mainViewModel.getUser().asLiveData().observe(this@MainActivity){
                    val token = "Bearer ${it.token}"
                    Log.d("Data Story Home", mainViewModel.getStories(token).toString())
                    mainViewModel.getStories(token).observe(this@MainActivity){data ->
                        adapter.submitData(lifecycle, data)
                    }
                }
                rvStories.layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                val loading = LoadingDialog(this)
                loading.startLoading()
                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle("Apakah anda yakin?")
                    setMessage("Apakah anda yakin untuk logout?")
                    setPositiveButton("Yakin") { _, _ ->
                        Intent(this@MainActivity, LoginActivity::class.java).also { intent ->
                            loading.dismissLoading()
                            startActivity(intent)
                            finish()
                        }
                        mainViewModel.logout()
                    }
                    setNegativeButton("Tidak") {_, _ ->
                        Toast.makeText(this@MainActivity, "Batal Logout", Toast.LENGTH_LONG).show()
                    }
                    create()
                    show()
                    loading.dismissLoading()
                }

            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.maps_menu -> {
                Log.d("Maps Click", "Clicked")
                Intent(this@MainActivity, MapsActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
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
    }

    private fun setupAction() {
        binding.apply {
            fabAddStory.setOnClickListener {
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupViewModel() {
        mainViewModel.getUser().asLiveData().observe(this@MainActivity) { user ->
            if (user.isLogin) {
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    companion object {
        const val SUCCESS_UPLOAD = "success upload story"
    }
}