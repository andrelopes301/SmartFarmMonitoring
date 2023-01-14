package com.ipv.farmmonitor.fragments

import android.annotation.SuppressLint

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.ipv.farmmonitor.*
import com.ipv.farmmonitor.databinding.FragmentItemBinding
import com.ipv.farmmonitor.models.Plantation
import com.ipv.farmmonitor.viewmodel.MainViewModel
import com.ipv.farmmonitor.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class Item( private var id: String) : Fragment(), MenuProvider {

    val db = Firebase.firestore
    @Inject
    lateinit var smartFarmingFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,smartFarmingFactory)[MainViewModel::class.java]
    }
    private lateinit var plantation: Plantation
    private val activity get() = context as MainActivity
    private val component get() = activity.component
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentItemBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            plantation = it.getSerializable("plantation") as Plantation
        }
        component.inject(this)
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        activity.supportActionBar?.title = plantation.name
        setPlantationHealthData(plantation)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setupItemView()

        binding.switchWaterPump.setOnClickListener {
            setWaterPump()
        }

        binding.switchSmartIrrigationSystem.setOnClickListener {
            setSmartIrrigationSystem()
        }


        listenForChanges()
    }



    private fun listenForChanges(){

            val docRef = db
                .collection("users")
                .document(getEmail())
                .collection("plantations")
                .document(id)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("TAG", "Data changed: ${snapshot.data}")

                    val json = Gson().toJson(snapshot.data)
                    val plantationModel = Gson().fromJson(json, Plantation::class.java)

                    plantation = plantationModel

                    setPlantationHealthData(plantation)

                } else {
                    Log.d("TAG", "Current data: null")
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setPlantationHealthData(data: Plantation){

        val handler = Handler(Looper.getMainLooper())
        handler.post {

            when (data.type) {
                "Tomatoes" -> binding.image.setImageResource(R.drawable.tomatoes)
                "Lettuces" -> binding.image.setImageResource(R.drawable.lettuce_farm)
                "Cucumber" -> binding.image.setImageResource(R.drawable.cucumber)
                "Beetroot" -> binding.image.setImageResource(R.drawable.beetroot)
                "Cabbages" -> binding.image.setImageResource(R.drawable.couves)
                "Corn" -> binding.image.setImageResource(R.drawable.corn)
                "Beans" -> binding.image.setImageResource(R.drawable.beans)
                "Apples" -> binding.image.setImageResource(R.drawable.apples)
                "Lentils" -> binding.image.setImageResource(R.drawable.lentils)
                "Citrus" -> binding.image.setImageResource(R.drawable.citrus)
                "Other" -> binding.image.setImageResource(R.drawable.other)
                else -> binding.image.setImageResource(R.drawable.other)
            }
            binding.switchWaterPump.isChecked = data.waterOn
            binding.switchSmartIrrigationSystem.isChecked = data.automaticWaterOn
            checkNotifications()

            if (data.waterOn)
                binding.switchWaterPumpDescription.text =
                    "The plantation is being watered!"
            else
                binding.switchWaterPumpDescription.text =
                    "The manual water system is turned OFF!"

            if (data.automaticWaterOn)
                binding.switchSmartIrrigationSystemDescription.text =
                    "The automatic water pump system is turned ON!"
            else
                binding.switchSmartIrrigationSystemDescription.text =
                    "The automatic water pump system is turned OFF!"

            if (data.readings.isNotEmpty()) {
                binding.lastTimeUpdated.text = getDate(data.readings.last().time)
                binding.temperature.text = "%.0f".format(data.readings.last().temperature) + "ºC"
                binding.moisture.text = "%.0f".format(data.readings.last().moisture) + "%"
                binding.light.text = "%.0f".format(data.readings.last().light) + "%"
                binding.humidity.text = "%.0f".format(data.readings.last().humidity) + "%"

                when (data.readings.last().moisture) {
                    in 0.0f..19.9f -> {
                        binding.statusHealthCard.setCardBackgroundColor(Color.parseColor("#C62828"))
                        binding.statusHealthText.text = "Low"
                        binding.statusHealthDescriptionText.text =
                            "Your plantation needs water!"
                    }
                    in 20.0f..39.9f -> {
                        binding.statusHealthCard.setCardBackgroundColor(Color.parseColor("#AFB42B"))
                        binding.statusHealthText.text = "Normal"
                        binding.statusHealthDescriptionText.text = "Your plantation is healthy!"
                    }
                    in 40.0f..84.9f -> {
                        binding.statusHealthCard.setCardBackgroundColor(Color.parseColor("#7CB342"))
                        binding.statusHealthText.text = "Good"
                        binding.statusHealthDescriptionText.text =
                            "Your plantation is very healthy!"
                    }
                    else -> {
                        binding.statusHealthCard.setCardBackgroundColor(Color.parseColor("#C62828"))
                        binding.statusHealthText.text = "Low"
                        binding.statusHealthDescriptionText.text =
                            "Your plantation is oversaturated!"
                    }
                }
            } else {
                binding.statusHealthCard.setCardBackgroundColor(Color.parseColor("#F57C00"))
                binding.statusHealthText.text = "Unknow"
                binding.statusHealthDescriptionText.text = "Data not found!"
                binding.light.text = "-"
                binding.humidity.text = "-"
                binding.moisture.text = "-"
                binding.temperature.text = "-"
            }
        }
    }

    private fun getDate(timestamp: Long): String {
        var dateTime = ""
        try {
            val df: DateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss")
            val date = Date(timestamp * 1000)
            dateTime = df.format(date)
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.d("Exception", it) }
        }
        return dateTime
    }

    private fun setupItemView(){
        val drawable = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_arrow_back
        )

        activity.supportActionBar?.setHomeAsUpIndicator(drawable)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        hideBottomBar()
    }

    private fun hideBottomBar(){

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)

        if(bottomNavigationView.visibility == View.VISIBLE){
            bottomNavigationView.animate()
                .translationY(bottomAppBar.height.toFloat())
                .alpha(0.0f)
                .duration = 300
            bottomNavigationView.visibility = View.GONE
        }
        if(bottomAppBar.visibility == View.VISIBLE){
            bottomAppBar.animate()
                .translationY(bottomAppBar.height.toFloat())
                .alpha(0.0f)
                .duration = 300
            bottomAppBar.visibility = View.GONE
        }
        if(fab.visibility == View.VISIBLE){
            fab.animate()
                .translationY(bottomAppBar.height.toFloat())
                .alpha(0.0f)
                .duration = 300
            fab.visibility = View.GONE
        }

    }

    private fun checkNotifications(){
        if (plantation.notificationsOn){
            viewModel.isNotificationsEnabled.postValue(true)
        }else
            viewModel.isNotificationsEnabled.postValue(false)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.item_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

        when(menuItem.itemId){
            android.R.id.home -> activity.supportFragmentManager.popBackStack()
            R.id.notifications_off -> setNotificationsOnOff()
            R.id.notifications_on -> setNotificationsOnOff()
            R.id.action_remove ->  removePlantation()
        }

        return true
    }

    override fun onPrepareMenu(menu: Menu) {

        viewModel.isNotificationsEnabled.observe(viewLifecycleOwner){ isEnabled ->
            menu.findItem(R.id.notifications_on).isVisible = isEnabled
            menu.findItem(R.id.notifications_off).isVisible = !isEnabled
        }
    }

    private fun setNotificationsOnOff(){
        db
            .collection("users")
            .document(getEmail())
            .collection("plantations")
            .document(id)
            .update("isNotificationsOn", !plantation.notificationsOn)
            .addOnSuccessListener {

                plantation.notificationsOn =  !plantation.notificationsOn
                viewModel.isNotificationsEnabled.postValue(plantation.notificationsOn)
                if (plantation.notificationsOn)
                    Toast.makeText(activity, "Notifications turned ON!", Toast.LENGTH_SHORT)
                        .show()
                else
                    Toast.makeText(activity, "Notifications turned OFF!", Toast.LENGTH_SHORT).show()


            }
            .addOnFailureListener { e ->
                Log.w("FIREBASE", "Error updating document", e)
            }
    }

    private fun setSmartIrrigationSystem(){
        val waterSystemOn = plantation.automaticWaterOn
        db
            .collection("users")
            .document(getEmail())
            .collection("plantations")
            .document(id)
            .update("automaticWaterOn", !waterSystemOn)
            .addOnSuccessListener {
                if (!waterSystemOn)
                    Toast.makeText(activity, "Smart irrigation system turned ON!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(activity, "Smart irrigation system turned OFF!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("FIREBASE", "Error adding document", e)
            }
    }



    private fun setWaterPump(){
        val waterOn = plantation.waterOn
        db
            .collection("users")
            .document(getEmail())
            .collection("plantations")
            .document(id)
            .update("waterOn", !waterOn)
            .addOnSuccessListener {
                if (!waterOn)
                    Toast.makeText(activity, "Water pump turned ON!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(activity, "Water pump turned OFF!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("FIREBASE", "Error adding document", e)
            }
    }

    private fun removePlantation(){
        val builder = AlertDialog.Builder(activity)

        builder.setTitle("Remove plantation")
        builder.setMessage("Do you wish to remove your plantation '${plantation.name}' ?")
        builder.setPositiveButton(HtmlCompat.fromHtml(("<font color='#ffffff'>OK</font>"), HtmlCompat.FROM_HTML_MODE_LEGACY))
        { _, _ ->
            db
                .collection("users")
                .document(getEmail())
                .collection("plantations")
                .document(id)
                .delete()
                .addOnSuccessListener {

                    Toast.makeText(activity, "Plantation '${plantation.name}' removed!", Toast.LENGTH_SHORT).show()
                    activity.supportFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Log.w("FIREBASE", "Error removing document", e)
                }
        }
        builder.setNegativeButton(HtmlCompat.fromHtml(("<font color='#ffffff'>Cancel</font>"), HtmlCompat.FROM_HTML_MODE_LEGACY)) { _, _ ->

        }
        builder.show()
    }


    private fun getEmail(): String {
        var email = ""
        if(auth.currentUser != null){
            email = auth.currentUser!!.email.toString()
        }
        return email
    }
}