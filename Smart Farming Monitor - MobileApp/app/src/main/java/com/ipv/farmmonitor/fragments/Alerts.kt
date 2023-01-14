package com.ipv.farmmonitor.fragments


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.farmmonitor.MainActivity
import com.ipv.farmmonitor.R
import com.ipv.farmmonitor.databinding.FragmentNotificationsBinding
import com.ipv.farmmonitor.viewmodel.MainViewModel
import com.ipv.farmmonitor.viewmodel.ViewModelFactory
import javax.inject.Inject


class Alerts : Fragment() , MenuProvider {

    @Inject
    lateinit var smartFarmingFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,smartFarmingFactory)[MainViewModel::class.java]
    }

    protected val activity get() = context as MainActivity
    private val component get() = activity.component
    lateinit var binding: FragmentNotificationsBinding
    lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.notificationsList.layoutManager = LinearLayoutManager(activity)
        binding.notificationsList.setHasFixedSize(true)

        activity.supportActionBar?.title = getString(R.string.notifications)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)


        //observe
        viewModel.notificationsAdapter.observe(viewLifecycleOwner){ notificationsAdapter ->
            binding.notificationsList.adapter = notificationsAdapter

            if (notificationsAdapter.itemCount == 0){
                binding.noNotifications.visibility = View.VISIBLE
                viewModel.hasNotifications.postValue(false)
            }else{
                binding.noNotifications.visibility = View.GONE
                viewModel.hasNotifications.postValue(true)
            }
        }
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.notifications_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

        when(menuItem.itemId){
            R.id.action_remove -> removePlantation()
        }

        return true
    }

    override fun onPrepareMenu(menu: Menu) {
        viewModel.hasNotifications.observe(viewLifecycleOwner){ hasNotifications ->
            menu.findItem(R.id.action_remove).isVisible = hasNotifications
        }
    }

    private fun removePlantation(){
        val builder = AlertDialog.Builder(activity)

        builder.setTitle("Remove notifications")
        builder.setMessage("Do you wish to remove all notifications?")
        builder.setPositiveButton(HtmlCompat.fromHtml(("<font color='#ffffff'>OK</font>"), HtmlCompat.FROM_HTML_MODE_LEGACY))
        { _, _ ->
            db
                .collection("users")
                .document(getEmail())
                .update("removeNotifications",true)
                .addOnSuccessListener {}
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