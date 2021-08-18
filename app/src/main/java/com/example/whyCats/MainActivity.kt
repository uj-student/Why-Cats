package com.example.whyCats

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.whyCats.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navHostFragment = findNavController(R.id.nav_host_fragment)
        setupNav(navHostFragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.uploadImageFragment, R.id.uploadHistoryFragment)
        )
        setupActionBarWithNavController(navHostFragment, appBarConfiguration)
    }

    private fun setupNav(navController: NavController) {
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomNav()
                R.id.uploadImageFragment -> showBottomNav()
                R.id.uploadHistoryFragment -> showBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        binding.navView.isVisible = true
    }
}