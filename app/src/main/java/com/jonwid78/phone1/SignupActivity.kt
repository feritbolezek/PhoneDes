package com.jonwid78.phone1

import android.app.ActivityOptions
import android.app.FragmentTransaction
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import android.text.method.TextKeyListener.clear



class SignupActivity : AppCompatActivity() {

    companion object {
        val TAG = "SignupActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        Log.d(TAG,"${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}${Calendar.getInstance().get(Calendar.MONTH) + 1}${Calendar.getInstance().get(Calendar.YEAR)}")
        val viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
//            val another = Calendar.getInstance()
//        another.set(2017,9,26,17,39,9)
//
//        val totest = Calendar.getInstance()
//        totest.set(2017,9,26,17,44,9)
//
//        Log.d(TAG, "${another.time}")
//        Log.d(TAG, "${totest.time}")
//
//        viewModel.loadEndResultsBetween(another.time, totest.time).observe(this, Observer {
//            Log.d(TAG, "NEW UPDATES $it")
//        })
       // viewModel.nukeEndResults()


            // viewModel.saveEndResults(EndResults(0,11.2,3.1,11.3,date.time))
//
//        viewModel.loadAllEndResults().observe(this, Observer {
//             Log.d(TAG, it!!.toString())
//        })

//        viewModel.loadInRecords().observe(this, Observer {
//            Log.d(TAG, it!!.toString())
//        })
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        Log.d(TAG, "FIRST DAY ${cal.get(Calendar.DAY_OF_MONTH) + 1} ${cal.get(Calendar.MONTH) + 1} ${cal.get(Calendar.YEAR)}")



         viewModel.loadUser().observe(this, Observer {
             if (it != null) {
                 startActivity(Intent(this,MainActivity::class.java))
             }
         })

        continueBtn.setOnClickListener {
            val intent = Intent(this,MainActivity().javaClass)
            intent.putExtra("UsersName",nameEditText.text.toString())
            val options = ActivityOptions.makeCustomAnimation(this,R.anim.abc_fade_in, R.anim.abc_fade_out)
            startActivity(intent,options.toBundle())
        }
    }

}
