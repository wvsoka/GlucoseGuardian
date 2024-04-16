package naszeAktywnosci

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.aplikacjatestowa.R
import com.google.firebase.auth.FirebaseAuth


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

        // Ustawienie nasłuchiwacza kliknięć dla przycisku logowania
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
                // Jeśli kliknięto registerTextViewClickable (przycisk przejścia do rejestracji), uruchom aktywność rejestracji
                // aby TextView mogł być klikalny,nalezy ustawić właściwą funkcję w pliku xml.
                R.id.button_signup -> {
                    val intent = Intent(this, RegistrationActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    // Walidacja danych logowania
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

    // Logowanie zarejestrowanego użytkownika
    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            val email = editTextLogin.text.toString().trim() { it <= ' ' }
            val password = editTextPassword.text.toString().trim() { it <= ' ' }

            // Logowanie za pomocą FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showErrorSnackBar(resources.getString(R.string.login_successfull), false) // text zdefiniowany w res -> values -> strings.xml
                        goToMainActivity()
                        finish()
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    private fun goToRegistration(){
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    // Przejście do aktywności głównej
    open fun goToMainActivity() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.email.toString()

        //Przekazanie wartości uid
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uID", uid)
        startActivity(intent)
    }
}