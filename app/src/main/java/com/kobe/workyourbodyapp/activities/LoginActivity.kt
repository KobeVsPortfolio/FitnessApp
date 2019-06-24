package com.kobe.workyourbodyapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kobe.workyourbodyapp.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toast_layout.view.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val toastView = layoutInflater.inflate(R.layout.toast_layout, null)
        val quotes = resources.getStringArray(R.array.quotes)

        random_quote.text = getRandomQuote(quotes)

        login_button.setOnClickListener {
            val email = email_edit_text.text.toString()
            val password = password_edit_text.text.toString()

            if(!email.isBlank() && !password.isBlank()) {
                signIn(email, password)
            }else{
                val toast = Toast(this)
                toastView.customToastText.text = getString(R.string.no_login_info)
                toast.view = toastView
                toast.show()
            }
        }

        guest_button.setOnClickListener {
            loginAsGuest()
        }

        register_button.setOnClickListener{
            signup()
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    val toastView = layoutInflater.inflate(R.layout.toast_layout, null)
                    val toast = Toast(this)
                    toastView.customToastText.text = getString(R.string.wrong_login)
                    toast.duration = Toast.LENGTH_LONG
                    toast.view = toastView
                    toast.show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
    }

    private fun signup(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(this, FragmentHolderActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getRandomQuote(quotes: Array<String>): String{
        return quotes.random()
    }

    private fun loginAsGuest(){
        val guestLoginAlert = AlertDialog.Builder(this)
        guestLoginAlert.setTitle("Login as Guest")
        guestLoginAlert.setMessage(
            "Are you sure you want to proceed as Guest? Your progress will not be saved."
        )

        guestLoginAlert.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            val intent = Intent(this, FragmentHolderActivity::class.java)
            startActivity(intent)
        }
        guestLoginAlert.setNegativeButton("Register") { dialogInterface: DialogInterface, i: Int ->
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val alertDialog: AlertDialog = guestLoginAlert.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getColor(R.color.colorPrimary))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getColor(R.color.colorPrimaryDark))
        val textView = alertDialog.findViewById<TextView>(android.R.id.message)
        val typeface = Typeface.createFromAsset(this.assets, "rubik_bold.ttf")
        textView?.typeface = typeface
        textView?.setTextColor(this.getColor(R.color.colorPrimaryDark))
        alertDialog.window?.setBackgroundDrawableResource(R.color.colorAccentWhite)
    }
}

