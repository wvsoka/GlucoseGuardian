package naszeAktywnosci

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplikacjatestowa.R
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegistrationActivity : AppCompatActivity() {
    // Deklaracje zmiennych dla pól widoku
    private var registerButton: Button? = null
    private var inputEmail: EditText? = null
    private var inputName: EditText? = null
    private var inputPassword: EditText? = null
    private var inputRepPass: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicjalizacja pól widoku
        registerButton = findViewById(R.id.registerButton)
        inputEmail = findViewById(R.id.inputLEmaill)
        inputName = findViewById(R.id.inputName)
        inputPassword = findViewById(R.id.inputPassword2)
        inputRepPass = findViewById(R.id.inputPassword2repeat)

        // Ustawienie nasłuchiwacza kliknięć dla przycisku rejestracji
        registerButton?.setOnClickListener{
            registerUser()
        }
    }

    // Walidacja danych rejestracji
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(inputEmail?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(inputName?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_name), true)
                false
            }
            TextUtils.isEmpty(inputPassword?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(inputRepPass?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_reppassword), true)
                false
            }
            inputPassword?.text.toString().trim {it <= ' '} != inputRepPass?.text.toString().trim{it <= ' '} -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_mismatch), true)
                false
            }
            else -> true
        }
    }

    // Przejście do aktywności logowania
    fun goToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Rejestracja użytkownika
    private fun registerUser() {
        if (validateRegisterDetails()) {
            val login: String = inputEmail?.text.toString().trim() {it <= ' '}
            val password: String = inputPassword?.text.toString().trim() {it <= ' '}

            // Utworzenie użytkownika w FirebaseAuth
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            showErrorSnackBar("You are registered successfully. Your user id is ${firebaseUser.uid}", false)

                            // Wylogowanie użytkownika i zakończenie aktywności
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