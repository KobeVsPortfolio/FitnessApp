package com.kobe.workyourbodyapp.domain

import android.os.Parcel
import android.os.Parcelable
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*


class User : Parcelable {

    var email: String? = null
    var name: String? = null
    var weight: Double = 0.0
    var length: Double = 0.0
    var age: Int = 0
    var gender: String? = null
    var workouts: List<Workout?> = listOf()

    fun getTotalCaloriesBurned(): Long {
        var burnedCalories: Long = 0
        if(!workouts.isNullOrEmpty()) {
            for (w in this.workouts) {
                burnedCalories += w?.calculateCaloriesBurned(weight)!!
            }
            return burnedCalories
        }
        return 0

    }

    fun getTotalAmountOfWorkouts(): Int {
        if(!workouts.isNullOrEmpty()) {
            return this.workouts.size
        }
        return 0
    }

    private fun getWorkoutsToday(): List<Workout>?{
        val workoutsToday: MutableList<Workout> = mutableListOf()
        for (w in this.workouts) {
            if (w?.isWorkoutToday()!!) {
                workoutsToday += w
            }
        }
        return workoutsToday
    }


    fun getWorkoutsFromCurrentWeek() : List<Workout?>{
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.clear(Calendar.MINUTE)
        cal.clear(Calendar.SECOND)
        cal.clear(Calendar.MILLISECOND)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val workoutsFromCurrentWeek : MutableList<Workout?> = mutableListOf()
        for(w in this.workouts){
            if(w?.date?.time!! > cal.time.time){
                workoutsFromCurrentWeek += w
            }
        }
        return workoutsFromCurrentWeek
    }

    fun getAmountOfWorkoutsToday(): Int {
        if(!getWorkoutsToday().isNullOrEmpty()){
            return getWorkoutsToday()?.size!!
        }
        return 0
    }

    fun getTotalCaloriesBurnedToday(): Long{
        var totalCaloriesBurnedToday : Long = 0
        if(!getWorkoutsToday().isNullOrEmpty()) {
            for (w in getWorkoutsToday()!!) {
                totalCaloriesBurnedToday += w.calculateCaloriesBurned(this.weight)
            }
            return totalCaloriesBurnedToday
        }
        return 0
    }

    fun getTotalMinutesOfWorkouts(): Long {
        var totalMinsOfWorkouts : Long = 0
        if(!workouts.isNullOrEmpty()) {
            for (w in workouts) {
                totalMinsOfWorkouts += w?.getDurationInMinutes()!!.toLong()
            }
            return totalMinsOfWorkouts
        }
        return 0
    }

    fun getTotalMinutesOfWorkoutsToday(): Long {
        var totalMinsOfWorkoutsToday: Long = 0
        if(!getWorkoutsToday().isNullOrEmpty()) {
            for (w in getWorkoutsToday()!!) {
                totalMinsOfWorkoutsToday += w.getDurationInMinutes().toLong()
            }
            return totalMinsOfWorkoutsToday
        }
        return 0
    }


    fun calculateBMI(): Double {
        val length = getLengthInMeters()
        val bmi = this.weight / (length * length)
        return BigDecimal(bmi).setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    private fun getLengthInMeters(): Double {
        return this.length / 100
    }

    constructor(parcel: Parcel) : this() {
        email = parcel.readString()
        name = parcel.readString()
        weight = parcel.readDouble()
        length = parcel.readDouble()
        age = parcel.readInt()
        gender = parcel.readString()
        workouts = listOf<Workout>().apply {
            parcel.readList(this, Workout::class.java.classLoader)
        }

    }

    constructor()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(name)
        parcel.writeDouble(weight)
        parcel.writeDouble(length)
        parcel.writeInt(age)
        parcel.writeString(gender)
        parcel.writeList(workouts)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}