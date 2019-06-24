package com.kobe.workyourbodyapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kobe.workyourbodyapp.R
import com.kobe.workyourbodyapp.domain.Difficulty
import com.kobe.workyourbodyapp.domain.User
import com.kobe.workyourbodyapp.domain.Workout
import douglasspgyn.com.github.circularcountdown.CircularCountdown
import douglasspgyn.com.github.circularcountdown.listener.CircularListener
import kotlinx.android.synthetic.main.activity_workout.*
import kotlinx.android.synthetic.main.fragment_edit_account.*
import kotlinx.android.synthetic.main.toast_layout.view.*
import java.util.*

class WorkoutActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("members")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        var currentWorkoutTime: Long = 0

        circularCountdown.create(0, 60, CircularCountdown.TYPE_SECOND)
            .listener(object : CircularListener {
                override fun onTick(progress: Int) {
                    currentWorkoutTime = progress.toLong()
                }

                override fun onFinish(newCycle: Boolean, cycleCount: Int) {
                    saveWorkout(currentWorkoutTime)
                }
            })
        circularCountdown.disableLoop()

        start_workout_button.setOnClickListener {
            circularCountdown.start()
        }

        end_workout_button.setOnClickListener {
            val guestLoginAlert = AlertDialog.Builder(this)
            guestLoginAlert.setTitle("Stop working out")
            guestLoginAlert.setMessage(
                "Are you sure you want to stop?"
            )

            guestLoginAlert.setPositiveButton("Yes") { _: DialogInterface, i: Int ->
                circularCountdown.stop()
                if (!currentWorkoutTime.equals(0)) {
                    saveWorkout(currentWorkoutTime)
                }
            }
            guestLoginAlert.setNegativeButton("Keep going") { dialogInterface: DialogInterface, i: Int ->
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

    fun saveWorkout(currentWorkoutTime: Long) {
        if (currentUser != null && currentUser.email != null) {
            userCollection.document("${currentUser.email}").get().addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!
                val workoutOne = Workout()
                workoutOne.date = Date()
                workoutOne.durationInSeconds = currentWorkoutTime * 60
                workoutOne.difficulty = Difficulty.BEGINNER
                val workouts = listOf(workoutOne)
                user.workouts += workouts
                db.collection("members")
                    .document(currentUser.email!!)
                    .set(user)
                    .addOnSuccessListener {
                        val intent = Intent(this, FragmentHolderActivity::class.java)
                        startActivity(intent)
                    }
            }
        }else{
            makeToast("No registered account found so progress hasn't been saved.", true)
            val intent = Intent(this, FragmentHolderActivity::class.java)
            startActivity(intent)
        }
    }

    fun makeToast(message: String, longToast: Boolean) {
        val toastView = layoutInflater.inflate(R.layout.toast_layout, null)
        val toast = Toast(this)
        toastView.customToastText.text = message
        if (longToast) {
            toast.duration = Toast.LENGTH_LONG
        }
        toast.duration = Toast.LENGTH_SHORT
        toast.view = toastView
        toast.show()
    }
}
