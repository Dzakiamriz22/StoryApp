package com.example.storyapp.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.data.model.Story
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.auth.LoginActivity
import com.example.storyapp.ui.maps.MapsActivity
import com.example.storyapp.ui.story.AddStoryActivity
import com.example.storyapp.ui.story.DetailStoryActivity
import com.example.storyapp.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)

        // Check if the user is logged in
        if (!isUserLoggedIn()) {
            navigateToLogin()
            return
        }

        setupRecyclerView()
        observePagedStories()
        setupActionBar()
        setupFab()
    }

    // Check if the user is logged in
    private fun isUserLoggedIn() = sharedPreferences.getString("token", "").isNullOrEmpty().not()

    // Navigate to login screen if not logged in
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // Setup the action bar
    private fun setupActionBar() {
        supportActionBar?.apply {
            title = getString(R.string.story_app)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    // Setup the floating action button (FAB) for adding new stories
    private fun setupFab() {
        binding.fabAdd.setOnClickListener { animateFabAndNavigate() }
    }

    // Animate the FAB and navigate to AddStoryActivity
    private fun animateFabAndNavigate() {
        binding.fabAdd.animate()
            .rotation(360f)
            .setDuration(500)
            .withEndAction {
                binding.fabAdd.rotation = 0f
                startActivity(Intent(this, AddStoryActivity::class.java))
            }
            .start()
    }

    // Setup the RecyclerView for displaying the list of stories
    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
            setHasFixedSize(true)
        }

        // Set up the item click listener for each story
        storyAdapter.setOnItemClickListener { story ->
            navigateToStoryDetail(story)
        }
    }

    // Navigate to the story detail screen
    private fun navigateToStoryDetail(story: Story) {
        val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
        intent.putExtra(DetailStoryActivity.EXTRA_STORY, story)
        startActivity(intent)
    }

    // Observe the paged stories from the ViewModel and submit to the adapter
    private fun observePagedStories() {
        val token = sharedPreferences.getString("token", "") ?: ""

        lifecycleScope.launch {
            // Collect the paging data from the ViewModel and submit it to the adapter
            viewModel.getPagedStories(token).collectLatest { pagingData ->
                storyAdapter.submitData(pagingData)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                logout()
                true
            }
            R.id.menu_maps -> {
                // Navigasi ke MapsActivity
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Perform the logout action and navigate to login screen
    private fun logout() {
        sharedPreferences.edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}