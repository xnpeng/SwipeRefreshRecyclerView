package com.anko.swiperefreshrecyclerview.model

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.DateTime

data class Media(val id: Int, val refId: Int? = -1, val fileName: String, val fileType: String? = "unknown", val title: String? = null, val sortOrder: Int? = 0)

data class Sweet(val id: Int, val userId: String, val text: String, val date: DateTime, val replyTo: Int = 0) : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Sweet> = object : Parcelable.Creator<Sweet> {
            override fun createFromParcel(source: Parcel): Sweet = Sweet(source)
            override fun newArray(size: Int): Array<Sweet?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readInt(), source.readString(), source.readString(), DateTime(source.readLong()), source.readInt())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(id)
        dest?.writeString(userId)
        dest?.writeString(text)
        dest?.writeLong(date.millis)
        dest?.writeInt(replyTo)
    }

    override fun describeContents() = 0
}

data class User(val userId: String, val mobile: String, val email: String, val displayName: String, val passwordHash: String)


