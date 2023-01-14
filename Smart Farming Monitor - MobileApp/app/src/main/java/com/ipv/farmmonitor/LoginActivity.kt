package com.ipv.farmmonitor

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ipv.farmmonitor.databinding.ActivityLoginBinding
import com.ipv.farmmonitor.viewmodel.MainViewModel
import com.ipv.farmmonitor.viewmodel.ViewModelFactory
import javax.inject.Inject


class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding
    val app get() = applicationContext as SmartFarmingApp
    val component by lazy { app.component.activityComponent() }
    private var isCreateUserMode = false
    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this,playStoreAppsFactory)[MainViewModel::class.java]
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils.fullScreen(this)
        auth = Firebase.auth


        binding.buttonCreateLogin.setOnClickListener { onLoginButtonClicked() }
        binding.buttonExistingAccount.setOnClickListener { toggleCreateUserMode() }

        checkTextChanges()

        viewModel.isLoginButton.observe(this){ isEnabled ->
            if (isEnabled){
                binding.buttonCreateLogin.isEnabled = true
                binding.buttonCreateLogin.isClickable = true
                binding.buttonCreateLogin.alpha = 1f
            }else {
                binding.buttonCreateLogin.isEnabled = false
                binding.buttonCreateLogin.isClickable = false
                binding.buttonCreateLogin.alpha = 0.5f
            }
        }


    }



    private fun checkTextChanges() {

        binding.inputEmail.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed.
                // You can check the new text value using s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed.

                if ((binding.inputEmail.text?.isNotEmpty() == true &&
                            binding.inputPassword.text?.isNotEmpty() == true))
                    viewModel.isLoginButton.postValue(true)
                else
                    viewModel.isLoginButton.postValue(false)
            }
        })

        binding.inputPassword.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed.
                // You can check the new text value using s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed.

                if ((binding.inputEmail.text?.isNotEmpty() == true &&
                            binding.inputPassword.text?.isNotEmpty() == true))
                    viewModel.isLoginButton.postValue(true)
                else
                    viewModel.isLoginButton.postValue(false)
            }
        })
    }


    private fun onLoginButtonClicked() {
        val username = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()
        if (isCreateUserMode) {
            registerUser(username, password)
        } else {
            logIn(username, password)
        }
    }


    private fun registerUser(email: String, password: String) {
        // while this operation completes, disable the button to login or create a new account
        binding.buttonCreateLogin.isEnabled = false
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")

                    Toast.makeText(this, "Authentication succeded!",
                        Toast.LENGTH_SHORT).show()


                    startActivity(Intent(application, MainActivity::class.java))

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
                binding.buttonCreateLogin.isEnabled = true
            }
    }

    private fun logIn(email: String, password: String) {
        // while this operation completes, disable the button to login or create a new account
        binding.buttonCreateLogin.isEnabled = false

        //val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
        //     Context.MODE_PRIVATE)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    Toast.makeText(this, "Login succeded!",
                        Toast.LENGTH_SHORT).show()


                    startActivity(Intent(application, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
                binding.buttonCreateLogin.isEnabled = true
            }


    }


    private fun toggleCreateUserMode() {
        this.isCreateUserMode = !isCreateUserMode
        if (isCreateUserMode) {
            binding.buttonCreateLogin.text = getString(R.string.create_account)
            binding.buttonExistingAccount.text = getString(R.string.already_have_account)
        } else {
            binding.buttonCreateLogin.text = getString(R.string.log_in)
            binding.buttonExistingAccount.text = getString(R.string.does_not_have_account)
        }
    }



    private fun displayErrorMessage(errorMsg: String) {
        Log.e(TAG(), errorMsg)
        Toast.makeText(baseContext, errorMsg, Toast.LENGTH_LONG).show()
    }


    private fun checkGooglePlayServices(): Boolean {
        // 1
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        // 2
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG(), "Error")
            // ask user to update google play services and manage the error.
            false
        } else {
            // 3
            Log.i(TAG(), "Google play services updated")
            true
        }
    }



}
