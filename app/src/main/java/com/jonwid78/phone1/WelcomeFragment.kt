package com.jonwid78.phone1


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_welcome.*


/**
 * A simple [Fragment] subclass.
 */
class WelcomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProviders.of(MainActivity()).get(UserViewModel::class.java)
        if (arguments.getString("UsersName") != null) {
            val user = User(1, arguments.getString("UsersName"), emptyList())
            viewModel.user = user
            viewModel.saveUser(user)
            welcomeTextView.text = getString(R.string.Welcome_label, viewModel.user?.name)
        } else {
            viewModel.loadUser().observe(this, Observer {
                welcomeTextView.text = getString(R.string.Welcome_label, it?.name)
            })
            viewModel.loadUser().removeObservers(this)
        }

    }

}// Required empty public constructor
