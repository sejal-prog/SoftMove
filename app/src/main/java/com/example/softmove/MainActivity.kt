package com.example.softmove

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.softmove.databinding.ActivityMainBinding
//import kotlinx.android.synthetic.main.activity_main.*
import nl.joery.animatedbottombar.AnimatedBottomBar
import org.jetbrains.annotations.NotNull


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        // Retrieve the name from the Intent
        val name = intent.getStringExtra("name")

        //loadFragment(HomeFragment())
        //navController = findNavController(R.id.activity_main_nav_host_fragment)

//
//        bottomBar.setOnItemSelectedListener { item ->
//            var fragment: Fragment
//            when (item) {
//                R.id.home -> {
//                    loadFragment(HomeFragment())
//                    true
//                }
//                R.id.discover -> {
//                    loadFragment(DiscoverFragment())
//                    true
//                }
//                R.id.dashboard -> {
//                    loadFragment(DashboardFragment())
//                    true
//                }
//                R.id.profile->{
//                        loadFragment(ProfileFragment())
//                        true
//                }
//                else->{
//
//                }
//            }
//        }

        if (savedInstanceState == null) {
            binding.animatedBottomBar.selectTabById(R.id.home, true)
            val homeFragment = HomeFragment()
            val bundle = Bundle()
            bundle.putString("name", name) // Pass the name to the HomeFragment
            homeFragment.arguments = bundle
            loadFragment(HomeFragment())

        }

            binding.animatedBottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                @Nullable lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                @NotNull newTab: AnimatedBottomBar.Tab
            ) {
                var fragment: Fragment? = null
                when (newTab.id) {
                    R.id.home -> {
                        val homeFragment = HomeFragment()
                        val bundle = Bundle()
                        bundle.putString("name", name) // Pass the name to the HomeFragment
                        homeFragment.arguments = bundle
                        loadFragment(HomeFragment())
                    }
                    R.id.discover -> fragment = DiscoverFragment()
                    R.id.dashboard -> fragment = DashboardFragment()
                    R.id.profile -> fragment = ProfileFragment()
                }
                if (fragment != null) {
                    loadFragment(fragment)
                } else {
                    Log.e(TAG, "Error in creating Fragment")
                }
            }
        })

    }
    private fun loadFragment(fragment: Fragment){

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}





