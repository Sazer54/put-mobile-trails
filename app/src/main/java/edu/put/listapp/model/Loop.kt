package edu.put.listapp.model

import android.os.Parcel
import android.os.Parcelable

data class Loop(
    val name: String,
    val distance: String,
    val steps: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(distance)
        parcel.writeString(steps)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Loop> {
        override fun createFromParcel(parcel: Parcel): Loop {
            return Loop(parcel)
        }

        override fun newArray(size: Int): Array<Loop?> {
            return arrayOfNulls(size)
        }
    }
}
