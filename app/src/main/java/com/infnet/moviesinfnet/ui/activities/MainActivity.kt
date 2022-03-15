package com.infnet.moviesinfnet.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.infnet.moviesinfnet.R
import com.infnet.moviesinfnet.databinding.ActivityMainBinding
import com.infnet.moviesinfnet.utils.helper.viewBinding
import com.infnet.moviesinfnet.vm.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity()

{
    private val viewModel: SearchViewModel by viewModels()
    private val binding by viewBinding(ActivityMainBinding::inflate)

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Infnet)
        setContentView(binding.root)

        setupCollecting()
        setupNavigation()
    }

    private fun signOut()
    {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun setupNavigation()
    {
        //set toolbar
        setSupportActionBar(binding.toolbar)

        // connect nav graph
        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        binding.bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        binding.bottomNavigationView.setOnNavigationItemReselectedListener { /*to not reload fragment again*/ }

        // beck button
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Timber.d("Destination: $destination")
            binding.appBarLayout.setExpanded(true)
        }

    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()

    private fun setupCollecting()
    {
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                if (it == null)
                {
                    signOut()
                }
            }
        }
    }
}