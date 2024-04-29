package edu.put.listapp.model

import android.os.Parcel
import android.os.Parcelable

data class Track(
    val name: String,
    var desc: String,
    val address: String,
    val loops: Map<String, Loop>,
    val thumbURL: String,
    val secondsElapsed: Int = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        mutableMapOf<String, Loop>().apply {
            parcel.readMap(this, Loop::class.java.classLoader)
        },
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(desc)
        parcel.writeString(address)
        parcel.writeMap(loops)
        parcel.writeString(thumbURL)
        parcel.writeInt(secondsElapsed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}
