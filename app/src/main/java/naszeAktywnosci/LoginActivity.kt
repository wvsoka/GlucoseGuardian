package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.example.aplikacjatestowa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import naszeAktywnosci.FirebaseData.FirestoreHandler


open class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var editTextLogin : EditText
    private lateinit var editTextPassword : EditText
    private lateinit var buttonLogin : Button
    private lateinit var buttonSignUp : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextLogin = findViewById(R.id.editText_login)
        editTextPassword = findViewById(R.id.editText_password)
        buttonLogin = findViewById(R.id.button_login)
        buttonSignUp = findViewById(R.id.button_signup)

        buttonLogin.setOnClickListener {
            logInRegisteredUser()
        }

        buttonSignUp.setOnClickListener {
            goToRegistration()
        }

    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.button_signup -> {
                    val intent = Intent(this, RegistrationActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(editTextLogin.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(editTextPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                showErrorSnackBar("Your details are valid", false)
                true
            }
        }
    }

    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            val email = editTextLogin.text.toString().trim() { it <= ' ' }
            val password = editTextPassword.text.toString().trim() { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser

                        if (user != null) {
                            val firestoreHandler = FirestoreHandler(FirebaseFirestore.getInstance())
                            
                            lifecycleScope.launch {
                                try {
                                    val userData = firestoreHandler.getUser(email)
                                    if (userData != null) {
                                        navigateBasedOnRole(userData.role)
                                    } else {
                                        showErrorSnackBar("User data not found in Firestore", true)
                                    }
                                } catch (e: Exception) {
                                    showErrorSnackBar("Error fetching user data: ${e.message}", true)
                                }
                            }
                        }
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    private fun navigateBasedOnRole(role: String?) {
        when (role) {
            "USER" -> {
                goToMainActivity()
            }
            "DOCTOR" -> {
                // todo
            }
            else -> {
                showErrorSnackBar("Undefined role or unauthorized access", true)
            }
        }
    }


    private fun goToRegistration(){
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    open fun goToMainActivity() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.email.toString()

        //Przekazanie wartości uid
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uID", uid)
        startActivity(intent)
    }
}