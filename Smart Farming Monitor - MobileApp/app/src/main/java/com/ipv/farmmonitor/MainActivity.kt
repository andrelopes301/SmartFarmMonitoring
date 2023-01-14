package com.ipv.farmmonitor

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.ipv.farmmonitor.adapter.NotificationsAdapter
import com.ipv.farmmonitor.adapter.PlantationsAdapter
import com.ipv.farmmonitor.databinding.ActivityMainBinding
import com.ipv.farmmonitor.fragments.*
import com.ipv.farmmonitor.models.Notification
import com.ipv.farmmonitor.models.Plantation
import com.ipv.farmmonitor.viewmodel.MainViewModel
import com.ipv.farmmonitor.viewmodel.ViewModelFactory
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val app get() = applicationContext as SmartFarmingApp
    val component by lazy { app.component.activityComponent() }
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    @Inject
    lateinit var smartFarmingDashboardFactory: ViewModelFactory<MainViewModel>
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this,smartFarmingDashboardFactory)[MainViewModel::class.java] }

    private val home = Home()
    private val search = Search()
    private val alerts = Alerts()
    private val settings = Settings()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setupInitialView()


        Firebase.messaging.isAutoInitEnabled = true
        requestPermissionLauncher

        binding.fab.setOnClickListener {
            val bottomSheetDialogFragment = BottomSheetFragment()
            bottomSheetDialogFragment.show(supportFragmentManager,bottomSheetDialogFragment.tag)
        }


        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.MenuHome -> replaceFragment(home)
                R.id.MenuSearch -> replaceFragment(search)
                R.id.MenuNotifications -> replaceFragment(alerts)
                R.id.MenuSettings -> replaceFragment(settings)
                else ->{
                }
            }
            true
        }
    }

    private fun setupInitialView(){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.primaryColor)))
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_menu_nav, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            android.R.id.home -> supportFragmentManager.popBackStack()
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()
        initPlantationsList()
        initNotificationsList()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            val data = hashMapOf("deviceToken" to token)
            db
                .collection("users")
                .document(getEmail())
                .set(data, SetOptions.merge())
            println("Token : $token")
        })


    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }



    private fun initPlantationsList(){
        db
            .collection("users")
            .document(getEmail())
            .collection("plantations")
            .get()
            .addOnSuccessListener { documents ->
                try {
                    if (documents != null) {
                        val plantations: MutableList<Plantation> = ArrayList()
                        val ids: ArrayList<String> = ArrayList()
                        for (document in documents) {
                            plantations.add(document.toObject(Plantation::class.java))
                            ids.add(document.id)
                        }
                        mainViewModel.plantationsAdapter.postValue(PlantationsAdapter(plantations,ids))
                        binding.progressBar.visibility = View.GONE
                        replaceFragment(home)
                        listenForListChanges()
                       // Toast.makeText(this, "DocumentSnapshot read successfully!", Toast.LENGTH_LONG).show()
                    } else {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                       // Toast.makeText(this, "No such document!", Toast.LENGTH_LONG).show()
                    }
                }catch (ex: Exception){
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    ex.message?.let { Log.e("Error", it) }
                }
            }.addOnFailureListener {
                    e ->
                Log.e("Error", "Error writing document", e)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

            }
    }

    private fun initNotificationsList(){
        db
            .collection("users")
            .document(getEmail())
            .collection("notifications")
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                try {
                    if (documents != null) {
                        val notifications: MutableList<Notification> = ArrayList()
                        for (document in documents) {
                            notifications.add(document.toObject(Notification::class.java))
                        }
                        notifications.reverse()
                        mainViewModel.notificationsAdapter.postValue(NotificationsAdapter(notifications))
                        //  binding.progressBar.visibility = View.GONE
                        listenForNotificationsListChanges()

                    }
                }catch (ex: Exception){
                    ex.message?.let { Log.e("Error", it) }
                }
            }.addOnFailureListener {
                    e -> Log.e("Error", "Error writing document", e)
            }
    }


    private fun listenForNotificationsListChanges(){
        val docRef = db
            .collection("users")
            .document(getEmail())
            .collection("notifications")
            .orderBy("time", Query.Direction.DESCENDING)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG(), "Listen failed.", e)
                return@addSnapshotListener
            }

            val notifications: MutableList<Notification> = ArrayList()
            for (document in snapshot?.documents!!) {
                document.toObject(Notification::class.java)?.let { notifications.add(it) }
            }
            mainViewModel.notificationsAdapter.postValue(NotificationsAdapter(notifications))
        }
    }


    private fun listenForListChanges(){
     //   CoroutineScope(Dispatchers.IO).launch {
            val docRef = db
                .collection("users")
                .document(getEmail())
                .collection("plantations")

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG(), "Listen failed.", e)
                    return@addSnapshotListener
                }

                val plantations: MutableList<Plantation> = ArrayList()
                val ids: ArrayList<String> = ArrayList()
                for (document in snapshot?.documents!!) {
                    document.toObject(Plantation::class.java)?.let { plantations.add(it) }
                    ids.add(document.id)
                }
                mainViewModel.plantationsAdapter.postValue(PlantationsAdapter(plantations,ids))
            }
      //  }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.plantationsAdapter.postValue(null)
    }

    private fun getEmail(): String {
        var email = ""
        if(auth.currentUser != null){
            email = auth.currentUser!!.email.toString()
        }
        return email
    }
}

