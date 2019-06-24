package com.kobe.workyourbodyapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.kobe.workyourbodyapp.R
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch
import com.kobe.workyourbodyapp.domain.User
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.toast_layout.view.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    var gender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.kobe.workyourbodyapp.R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        toggle_button.onChangeListener = object : ToggleSwitch.OnChangeListener {
            override fun onToggleSwitchChanged(position: Int) {
                if (position == 0) {
                    gender = "Man"
                } else if (position == 1) {
                    gender = "Woman"
                }
            }
        }

        register_confirm_button.setOnClickListener {
            val firstName = firstName_edit_text.text.toString()
            val lastName = lastName_edit_text.text.toString()
            val email = email_edit_text.text.toString().toLowerCase()
            val password = password_edit_text.text.toString()
            val repeatPassword = repeatPassword_edit_text.text.toString()
            val weight = weight_edit_text.text.toString()
            val length = length_edit_text.text.toString()
            val age = age_edit_text.text.toString()
            if ((
                        !firstName.isBlank()
                                || !lastName.isBlank()
                                || !email.isBlank()
                                || !password.isBlank()
                                || !weight.isBlank()
                                || !length.isBlank()
                                || !age.isBlank())
            ){
                val displayName = "$firstName $lastName"
                if (password == repeatPassword) {
                    if (password.length > 5) {
                        createAccount(email, password, displayName, weight, length, age, gender)
                    } else {
                        makeToast(getString(R.string.password_6_chars), true)
                    }
                } else {
                    makeToast(getString(R.string.passwords_no_match), true)
                }
            }else{
                makeToast(getString(R.string.fill_all_fields), true)
            }
        }

        back_button.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(email: String, password: String, displayName: String, weight: String, length: String, age: String, gender: String?) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this)
            { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(displayName).build())
                    addUser(email, displayName, weight, length, age, gender)
                    makeToast(getString(R.string.account_created), false)
                    updateUI(currentUser)
                } else {
                    makeToast(getString(R.string.email_invalid), true)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addUser(email: String, displayName: String, weight: String, length: String, age: String, gender: String?){
                val user = User()
                user.name = displayName
                user.email = email
                user.weight = weight.toDouble()
                user.length = length.toDouble()
                user.age = age.toInt()
                user.gender = gender
                db.collection("members").document(email)
                    .set(user)
                    .addOnSuccessListener { documentReference ->
                    }
                    .addOnFailureListener { e ->
                        makeToast("There went something wrong, you can log in but your progress won't be saved this session.", true)
                    }
        }

    private fun makeToast(message: String, longToast: Boolean){
        val toastView = layoutInflater.inflate(R.layout.toast_layout, null)
        val toast = Toast(this)
        toastView.customToastText.text = message
        if(longToast){
            toast.duration = Toast.LENGTH_LONG
        }
        toast.duration = Toast.LENGTH_SHORT
        toast.view = toastView
        toast.show()
    }
}
