package com.kobe.workyourbodyapp.activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kobe.workyourbodyapp.R
import com.kobe.workyourbodyapp.domain.User
import com.kobe.workyourbodyapp.fragments.AccountFragment
import com.kobe.workyourbodyapp.fragments.EditAccountFragment
import com.kobe.workyourbodyapp.fragments.WorkoutFragment
import com.kobe.workyourbodyapp.fragments.ProgressFragment
import kotlinx.android.synthetic.main.activity_fragment_holder.*
import kotlinx.android.synthetic.main.toast_layout.view.*

class FragmentHolderActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val manager = supportFragmentManager
    private val currentUser = auth.currentUser
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("members")
    val accountFragment = AccountFragment()
    val progressFragment = ProgressFragment()
    val workoutFragment = WorkoutFragment()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_holder)

        if (currentUser != null && currentUser.email != null) {
            userCollection.document("${currentUser.email}").get().addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    sendData(accountFragment, user)
                    sendData(progressFragment, user)
                }
            }
        }
        progressBar.visibility = View.INVISIBLE
        showWorkoutFragment()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_workout
        navView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_workout -> {
                    navView.itemTextColor = resources.getColorStateList(R.drawable.bottom_nav_workout_selected, theme)
                    navView.itemIconTintList = resources.getColorStateList(R.drawable.bottom_nav_workout_selected, theme)
                    showWorkoutFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_progress -> {
                    navView.itemTextColor = resources.getColorStateList(R.drawable.bottom_nav_progress_selected, theme)
                    navView.itemIconTintList = resources.getColorStateList(R.drawable.bottom_nav_progress_selected, theme)
                    showProgressFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_account -> {
                    navView.itemTextColor = resources.getColorStateList(R.drawable.bottom_nav_account_selected, theme)
                    navView.itemIconTintList = resources.getColorStateList(R.drawable.bottom_nav_account_selected, theme)
                    showAccountFragment()
                    return@OnNavigationItemSelectedListener true

                }
            }
            true
        })
    }

    @SuppressLint("ResourceType")
    fun showWorkoutFragment(){
        nav_view.itemTextColor = resources.getColorStateList(R.drawable.bottom_nav_workout_selected, theme)
        nav_view.itemIconTintList = resources.getColorStateList(R.drawable.bottom_nav_workout_selected, theme)
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_holder, workoutFragment)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    fun showProgressFragment(){
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_holder, progressFragment)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    fun showAccountFragment(){
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_holder, accountFragment)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        val exitAppAlert = AlertDialog.Builder(this)
        exitAppAlert.setTitle("Exit Application")
        exitAppAlert.setMessage(
            "Are you sure you want to exit the application?"
        )

        exitAppAlert.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            finishAffinity()
        }
        exitAppAlert.setNegativeButton("Work hard, play later") { dialogInterface: DialogInterface, i: Int ->
        }

        val alertDialog: AlertDialog = exitAppAlert.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getColor(R.color.colorPrimary))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getColor(R.color.colorPrimaryDark))
        val textView = alertDialog.findViewById<TextView>(android.R.id.message)
        val typeface = Typeface.createFromAsset(this.assets, "rubik_bold.ttf")
        textView?.typeface = typeface
        textView?.setTextColor(this.getColor(R.color.colorPrimaryDark))
        alertDialog.window?.setBackgroundDrawableResource(R.color.colorAccentWhite)
    }

    private fun sendData(fragment: Fragment, user: User){
        val bundle = Bundle()
        bundle.putParcelable("USER_KEY", user)
        fragment.arguments = bundle
    }
}
