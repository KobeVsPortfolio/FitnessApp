package com.kobe.workyourbodyapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

import com.kobe.workyourbodyapp.R
import com.kobe.workyourbodyapp.activities.FragmentHolderActivity
import com.kobe.workyourbodyapp.activities.WorkoutActivity
import com.kobe.workyourbodyapp.domain.Difficulty
import com.kobe.workyourbodyapp.domain.User
import com.kobe.workyourbodyapp.domain.Workout
import kotlinx.android.synthetic.main.fragment_workout.*
import java.lang.NullPointerException
import java.util.*


class WorkoutFragment : Fragment() {

    private var auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beginner_button.setOnClickListener {
            val intent = Intent(activity, WorkoutActivity::class.java)
            startActivity(intent)
        }

        advanced_button.setOnClickListener {
            val intent = Intent(activity, WorkoutActivity::class.java)
            startActivity(intent)
        }

        expert_button.setOnClickListener {
            val intent = Intent(activity, WorkoutActivity::class.java)
            startActivity(intent)
        }

    }

}
