package com.kobe.workyourbodyapp.domain

import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class Workout : Parcelable{

    var durationInSeconds: Long = 0
    var date: Date? = Date()
    var difficulty : Difficulty? = null

    fun getDurationInMinutes(): Double{
        val durationInMinutes = (durationInSeconds/60)
        return BigDecimal(durationInMinutes).setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }
    fun calculateCaloriesBurned(weight: Double): Long{
        when(difficulty){

            Difficulty.BEGINNER -> {
                val burnedCalories = (getDurationInMinutes() * 3.5 * Difficulty.BEGINNER.metValue * weight) / 200
                return BigDecimal(burnedCalories).setScale(2, RoundingMode.HALF_EVEN).toLong()
            }
            Difficulty.ADVANCED -> {
                val burnedCalories = (getDurationInMinutes() * 3.5 * Difficulty.ADVANCED.metValue * weight) / 200
                return BigDecimal(burnedCalories).setScale(2, RoundingMode.HALF_EVEN).toLong()
            }
            Difficulty.EXPERT -> {
                val burnedCalories = (getDurationInMinutes() * 3.5 * Difficulty.EXPERT.metValue * weight) / 200
                return BigDecimal(burnedCalories).setScale(2, RoundingMode.HALF_EVEN).toLong()
            }
            null -> return 0
        }

    }

    fun getWeekDay(): String? {
        val day = this.date.toString().split(" ")
        return day[0]
    }

    fun isWorkoutToday(): Boolean{
        return DateUtils.isToday(this.date!!.time)
    }

    constructor(parcel: Parcel) : this() {
        durationInSeconds = parcel.readLong()
        date?.time = parcel.readLong()
    }

    constructor()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(durationInSeconds)
        parcel.writeLong(date?.time!!)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Workout> {
        override fun createFromParcel(parcel: Parcel): Workout {
            return Workout(parcel)
        }

        override fun newArray(size: Int): Array<Workout?> {
            return arrayOfNulls(size)
        }
    }
}