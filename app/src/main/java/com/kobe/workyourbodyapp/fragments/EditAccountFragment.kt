package com.kobe.workyourbodyapp.fragments

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kobe.workyourbodyapp.R
import com.kobe.workyourbodyapp.activities.FragmentHolderActivity
import com.kobe.workyourbodyapp.activities.RegisterActivity
import com.kobe.workyourbodyapp.domain.User
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_edit_account.*
import kotlinx.android.synthetic.main.toast_layout.view.*


class EditAccountFragment : Fragment() {

    private var auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("members")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user : User? = arguments?.getParcelable("USER_KEY")
        name_edit_text.text = SpannableStringBuilder(user?.name)
        weight_edit_text.text = SpannableStringBuilder(user?.weight.toString())
        length_edit_text.text = SpannableStringBuilder(user?.length.toString())
        age_edit_text.text = SpannableStringBuilder(user?.age.toString())
        if (user?.gender == "Man") {
            gender_toggle.setCheckedPosition(0)
        } else if (user?.gender == "Woman") {
            gender_toggle.setCheckedPosition(1)
        }

        back_button.setOnClickListener {
            goToAccountFragment(user)
        }

        save_account_info_button.setOnClickListener {
                    val userInfo = HashMap<String, Any>()
                    userInfo["name"] = name_edit_text.text.toString()
                    userInfo["weight"] = weight_edit_text.text.toString().toDouble()
                    userInfo["length"] = length_edit_text.text.toString().toDouble()
                    userInfo["age"] = age_edit_text.text.toString().toInt()
                    if (gender_toggle.checkedPosition == 0) {
                        userInfo["gender"] = "Man"
                    } else if (gender_toggle.checkedPosition == 1) {
                        userInfo["gender"] = "Woman"
                    }
                    db.collection("members").document(currentUser?.email!!)
                        .set(userInfo, SetOptions.merge())
                        .addOnSuccessListener {
                            val intent = Intent(activity, FragmentHolderActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            makeToast("Something went wrong while saving data, please try again.", true)
                        }
                }
            }


    private fun goToAccountFragment(user: User?) {
        val bundle = Bundle()
        bundle.putParcelable("USER_KEY", user)
        val fragment = AccountFragment()
        fragment.arguments = bundle
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_holder, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun makeToast(message: String, longToast: Boolean) {
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
