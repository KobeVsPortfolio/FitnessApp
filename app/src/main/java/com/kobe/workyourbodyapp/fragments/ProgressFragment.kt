package com.kobe.workyourbodyapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.fragment_progress.*
import com.hadiidbouk.charts.BarData
import com.kobe.workyourbodyapp.domain.User
import com.kobe.workyourbodyapp.domain.Workout
import java.util.*


class ProgressFragment : Fragment() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.kobe.workyourbodyapp.R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (currentUser != null && currentUser.email != null) {

            val user: User? = arguments?.getParcelable("USER_KEY")

            workouts_total.text = user?.getTotalAmountOfWorkouts().toString()
            minutes_total.text = user?.getTotalMinutesOfWorkouts().toString()
            kcal_total.text = user?.getTotalCaloriesBurned().toString()
            workouts_today.text = user?.getAmountOfWorkoutsToday().toString()
            minutes_today.text = user?.getTotalMinutesOfWorkoutsToday().toString()
            kcal_today.text = user?.getTotalCaloriesBurnedToday().toString()

            val mondayWorkouts: MutableList<Workout> = mutableListOf()
            val tuesdayWorkouts: MutableList<Workout> = mutableListOf()
            val wednesdayWorkouts: MutableList<Workout> = mutableListOf()
            val thursdayWorkouts: MutableList<Workout> = mutableListOf()
            val fridayWorkouts: MutableList<Workout> = mutableListOf()
            val saturdayWorkouts: MutableList<Workout> = mutableListOf()
            val sundayWorkouts: MutableList<Workout> = mutableListOf()

            for (w in user?.getWorkoutsFromCurrentWeek()!!) {
                when (w?.getWeekDay()) {
                    "Mon" -> {
                        mondayWorkouts += w
                    }
                    "Tue" -> {
                        tuesdayWorkouts += w
                    }
                    "Wed" -> {
                        wednesdayWorkouts += w
                    }
                    "Thu" -> {
                        thursdayWorkouts += w
                    }
                    "Fri" -> {
                        fridayWorkouts += w
                    }
                    "Sat" -> {
                        saturdayWorkouts += w
                    }
                    "Sun" -> {
                        sundayWorkouts += w
                    }
                }
            }

            val dataList = arrayListOf<BarData>()

            var data = BarData(
                "Mon",
                getTotalMinutesOfWorkoutOfDay(mondayWorkouts).toFloat(),
                getTotalMinutesOfWorkoutOfDay(mondayWorkouts).toString()
            )
            dataList.add(data)

            data = BarData(
                "Tue",
                getTotalMinutesOfWorkoutOfDay(tuesdayWorkouts).toFloat(),
                getTotalMinutesOfWorkoutOfDay(tuesdayWorkouts).toString()
            )
            dataList.add(data)

            data = BarData(
                "Wed",
                getTotalMinutesOfWorkoutOfDay(wednesdayWorkouts).toFloat(),
                getTotalMinutesOfWorkoutOfDay(wednesdayWorkouts).toString()
            )
            dataList.add(data)

            data = BarData(
                "Thu",
                getTotalMinutesOfWorkoutOfDay(thursdayWorkouts).toFloat(),
                getTotalMinutesOfWorkoutOfDay(thursdayWorkouts).toString()
            )
            dataList.add(data)

            data = BarData(
                "Fri",
                getTotalMinutesOfWorkoutOfDay(fridayWorkouts).toFloat(),
                getTotalMinutesOfWorkoutOfDay(fridayWorkouts).toString()
            )
            dataList.add(data)

            data = BarData(
                "Sat",
                getTotalMinutesOfWorkoutOfDay(saturdayWorkouts).toFloat(),
                getTotalMinutesOfWorkoutOfDay(saturdayWorkouts).toString()
            )
            dataList.add(data)

            data = BarData(
                "Sun",
                getTotalMinutesOfWorkoutOfDay(sundayWorkouts).toFloat(),
                getTotalMinutesOfWorkoutOfDay(sundayWorkouts).toString()
            )
            dataList.add(data)

            chartProgressBar.setDataList(dataList)
            chartProgressBar.build()
        } else {
            workouts_total.text = "0"
            minutes_total.text = "0"
            kcal_total.text = "0"
            workouts_today.text = "0"
            minutes_today.text = "0"
            kcal_today.text = "0"
        }
    }
    fun getTotalMinutesOfWorkoutOfDay(workouts: List<Workout>): Int {
        var totalMinsOfWorkouts = 0
        for (w in workouts) {
            totalMinsOfWorkouts += w.getDurationInMinutes().toInt()
        }
        if (totalMinsOfWorkouts > 30) {
            return 60
        }
        return totalMinsOfWorkouts
    }
}

