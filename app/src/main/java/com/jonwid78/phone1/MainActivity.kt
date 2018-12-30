package com.jonwid78.phone1

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.container,HomeFragment(),null)
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_calendar -> {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                        .replace(R.id.container,CalendarFragment(),null)
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                        .replace(R.id.container,SettingsFragment(),null)
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        // GoogleApiClient.Builder(this).addApi().build()

        val viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        navigation.visibility = View.GONE

        welcomeUser()
    }

    fun welcomeUser() {
        val intent = getIntent()
        val args = Bundle()
        args.putString("UsersName",intent.getStringExtra("UsersName"))

        val welcomeFragment = WelcomeFragment()
        welcomeFragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.container,welcomeFragment,null).commit()
        replaceView(2000L)
    }

    fun replaceView(time: Long) {
        val mainHandler = Handler(this.mainLooper)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                        .replace(R.id.container,HomeFragment(),null)
                        .commit()
                cancel()
                mainHandler.post {
                    navigation.visibility = View.VISIBLE
                }
            }
        }, time)
    }

}
