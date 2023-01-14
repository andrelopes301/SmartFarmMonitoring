package com.ipv.farmmonitor.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.farmmonitor.MainActivity
import com.ipv.farmmonitor.R
import com.ipv.farmmonitor.databinding.FragmentBottomSheetBinding
import com.ipv.farmmonitor.models.Plantation


class BottomSheetFragment : BottomSheetDialogFragment() {
    private val activity get() = context as MainActivity
    private val db = Firebase.firestore
    private lateinit var binding: FragmentBottomSheetBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plants = resources.getStringArray(R.array.plants)
        val arrayAdapter = ArrayAdapter(activity, R.layout.dropdown_item, plants)
        binding.plantationType.setAdapter(arrayAdapter)

        binding.addPlantation.setOnClickListener {
            val name = binding.plantationName.text.toString()
            val type = binding.plantationType.text.toString()
            val id = System.currentTimeMillis().toString()

            val plantation = Plantation(
                id = id,
                name = name,
                type = type,
                notificationsOn = true,
                automaticWaterOn = true,
                readings = arrayListOf()
            )


            db
                .collection("users")
                .document(getEmail())
                .collection("plantations")
                .document(id)
                .set(plantation)
                .addOnSuccessListener {
                    Log.d("FIREBASE", "DocumentSnapshot added with ID: $id")
                    dialog?.hide()

                    Toast.makeText(
                        activity,
                        "Plantation '${name}' created!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                .addOnFailureListener { e ->
                    Log.w("FIREBASE", "Error adding document", e)
                }
        }


    }


    private fun getEmail(): String {
        var email = ""
        if(auth.currentUser != null){
            email = auth.currentUser!!.email.toString()
        }
        return email
    }
}