package com.kobe.workyourbodyapp.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kobe.workyourbodyapp.R
import com.kobe.workyourbodyapp.activities.FragmentHolderActivity
import com.kobe.workyourbodyapp.activities.LoginActivity
import com.kobe.workyourbodyapp.activities.RegisterActivity
import com.kobe.workyourbodyapp.domain.User
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.toast_layout.view.*

class AccountFragment : Fragment() {

    private var auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("members")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (currentUser != null && currentUser.email != null) {
            return inflater.inflate(R.layout.fragment_account, container, false)
        } else {
            return inflater.inflate(R.layout.fragment_account_guest, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user: User? = arguments?.getParcelable("USER_KEY")
        if (user != null) {
            name_text.text = SpannableStringBuilder(user.name)
            weight_text.text = SpannableStringBuilder(user.weight.toString())
            height_text.text = SpannableStringBuilder(user.length.toString())
            age_text.text = SpannableStringBuilder(user.age.toString())
            bmi_text.text = SpannableStringBuilder(user.calculateBMI().toString())
        }

        change_password_button.setOnClickListener {
            changePassword()
        }

        delete_account_button.setOnClickListener {
            clickDelete()
        }

        logout_button.setOnClickListener {
            signOut()
        }

        edit_account_button.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("USER_KEY", user)
            val fragment = EditAccountFragment()
            fragment.arguments = bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_holder, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }

    private fun changePassword() {
        if (oldPassword_edit_text.text!!.isNotBlank() &&
            newPassword_edit_text.text!!.isNotBlank() &&
            confirmNewPassword_edit_text.text!!.isNotBlank()
        ) {
            if (newPassword_edit_text.text.toString() == confirmNewPassword_edit_text.text.toString()) {
                val user = auth.currentUser
                if (user != null && user.email != null) {
                    val credential = EmailAuthProvider
                        .getCredential(user.email!!, oldPassword_edit_text.text.toString())
                    user.reauthenticate(credential)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                user.updatePassword(newPassword_edit_text.text.toString())
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            makeToast("Password succesfully changed, please log in again.", true)
                                            auth.signOut()
                                            val intent = Intent(activity, LoginActivity::class.java)
                                            startActivity(intent)
                                            activity?.finish()
                                        }
                                    }
                            } else {
                                oldPassword_edit_text.text = null
                                newPassword_edit_text.text = null
                                confirmNewPassword_edit_text.text = null
                                makeToast("Incorrect Password", false)
                            }
                        }

                } else {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()
                }

            } else {
                makeToast("Passwords don't match.", false)
            }

        } else {
            makeToast("Please fill in all the fields.", false)
        }
    }

    private fun deleteAccount() {
        val password = oldPassword_edit_text.text.toString()
        if (!password.isBlank()) {
            val credential = EmailAuthProvider
                .getCredential(currentUser?.email!!, password)
            currentUser.reauthenticate(credential)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        auth.currentUser?.delete()
                        makeToast("${currentUser.email} has been deleted.", true)
                        val intent = Intent(activity, LoginActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        makeToast("Incorrect password.", true)
                    }
                }
        } else {
            makeToast("Please fill in your current password.", true)
        }
    }

    private fun signOut() {
        auth.signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun clickDelete() {
        val guestLoginAlert = AlertDialog.Builder(activity!!)
        guestLoginAlert.setTitle("DELETE ACCOUNT")
        guestLoginAlert.setMessage(
            "Are you sure you want to proceed?"
        )

        guestLoginAlert.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            deleteAccount()
        }
        guestLoginAlert.setNegativeButton("No, i'd rather keep my progress") { dialogInterface: DialogInterface, i: Int ->
        }

        val alertDialog: AlertDialog = guestLoginAlert.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity!!.getColor(R.color.colorWarning))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(activity!!.getColor(R.color.colorPrimaryDark))
        val textView = alertDialog.findViewById<TextView>(android.R.id.message)
        val typeface = Typeface.createFromAsset(activity!!.assets, "rubik_bold.ttf")
        textView?.typeface = typeface
        textView?.setTextColor(activity!!.getColor(R.color.colorPrimaryDark))
        alertDialog.window?.setBackgroundDrawableResource(R.color.colorAccentWhite)
    }


    fun makeToast(message: String, longToast: Boolean) {
        val toastView = layoutInflater.inflate(R.layout.toast_layout, null)
        val toast = Toast(activity)
        toastView.customToastText.text = message
        if (longToast) {
            toast.duration = Toast.LENGTH_LONG
        }
        toast.duration = Toast.LENGTH_SHORT
        toast.view = toastView
        toast.show()
    }
}
