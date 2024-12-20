package naszeAktywnosci

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.aplikacjatestowa.R
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.Intent
import com.google.android.gms.tasks.OnCompleteListener


class RegistrationActivity : BaseActivity() {
    private lateinit var registerButton: Button
    private lateinit var inputEmail: EditText
    private lateinit var inputName: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputRepPass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        registerButton = findViewById(R.id.button_signin)
        inputEmail = findViewById(R.id.editTextTextEMail)
        inputName = findViewById(R.id.editTextTextName)
        inputPassword = findViewById(R.id.editTextTextPassword)
        inputRepPass = findViewById(R.id.editTextTextRepeatPassword)

        registerButton.setOnClickListener{
            registerUser()
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(inputEmail.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(inputName.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_name), true)
                false
            }
            TextUtils.isEmpty(inputPassword.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(inputRepPass.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_reppassword), true)
                false
            }
            inputPassword.text.toString().trim {it <= ' '} != inputRepPass.text.toString().trim{it <= ' '} -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_mismatch), true)
                false
            }
            else -> true
        }
    }


    fun goToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun registerUser() {
        if (validateRegisterDetails()) {
            val login: String = inputEmail?.text.toString().trim() {it <= ' '}
            val password: String = inputPassword?.text.toString().trim() {it <= ' '}
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            showErrorSnackBar("You are registered successfully. Your user id is ${firebaseUser.uid}", false)

                            FirebaseAuth.getInstance().signOut()
                            finish()
                        } else {
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
                )
        }
    }
}