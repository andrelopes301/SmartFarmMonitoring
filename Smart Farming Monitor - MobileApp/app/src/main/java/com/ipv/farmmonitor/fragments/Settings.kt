package com.ipv.farmmonitor.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.farmmonitor.LoginActivity
import com.ipv.farmmonitor.MainActivity
import com.ipv.farmmonitor.R
import com.ipv.farmmonitor.databinding.FragmentSettingsBinding
import com.ipv.farmmonitor.viewmodel.MainViewModel
import com.ipv.farmmonitor.viewmodel.ViewModelFactory
import javax.inject.Inject
import kotlin.random.Random

class Settings : Fragment() {

    private val activity get() = context as MainActivity
    private val component get() = activity.component
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        when (Random.nextInt(0, 10)) {
            0 -> binding.profileImage.setImageResource(R.drawable.tomatoes)
            1-> binding.profileImage.setImageResource(R.drawable.lettuce_farm)
            2 -> binding.profileImage.setImageResource(R.drawable.cucumber)
            3 -> binding.profileImage.setImageResource(R.drawable.beetroot)
            4 -> binding.profileImage.setImageResource(R.drawable.couves)
            5 -> binding.profileImage.setImageResource(R.drawable.corn)
            6 -> binding.profileImage.setImageResource(R.drawable.beans)
            7 -> binding.profileImage.setImageResource(R.drawable.apples)
            8 -> binding.profileImage.setImageResource(R.drawable.lentils)
            9 -> binding.profileImage.setImageResource(R.drawable.citrus)
            else -> binding.profileImage.setImageResource(R.drawable.other)
        }


        binding.profileUsername.text = auth.currentUser?.email ?: getString(R.string.profile)

        binding.account.categoryText.text = getString(R.string.account)
        binding.account.cardImage.setImageResource(R.drawable.person)


        binding.plantations.categoryText.text = getString(R.string.plantations)
        binding.plantations.cardImage.setImageResource(R.drawable.plantations)

        binding.notifications.categoryText.text = getString(R.string.notifications)
        binding.notifications.cardImage.setImageResource(R.drawable.notifications)

        binding.about.categoryText.text = getString(R.string.about)
        binding.about.cardImage.setImageResource(R.drawable.about)

        binding.logout.categoryText.text = getString(R.string.log_out)
        binding.logout.cardImage.setImageResource(R.drawable.logout)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity.supportActionBar?.title = getString(R.string.settings)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        showBottomBar()

        binding.plantations.listItem.setOnClickListener {
            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView.selectedItemId = R.id.MenuHome
        }

        binding.notifications.listItem.setOnClickListener {
            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView.selectedItemId = R.id.MenuNotifications
        }


        binding.logout.listItem.setOnClickListener {
            Firebase.auth.signOut()
            goToLoginActivity()
        }
    }


    private fun showBottomBar(){
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)

        if(bottomNavigationView.visibility == View.GONE){
            bottomNavigationView.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            bottomNavigationView.visibility = View.VISIBLE
        }
        if(bottomAppBar.visibility == View.GONE){
            bottomAppBar.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            bottomAppBar.visibility = View.VISIBLE
        }
        if(fab.visibility == View.GONE){
            fab.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            fab.visibility = View.VISIBLE
        }
    }


    private fun goToLoginActivity(){

        startActivity(Intent(activity, LoginActivity::class.java))
        activity.finish()
    }

}