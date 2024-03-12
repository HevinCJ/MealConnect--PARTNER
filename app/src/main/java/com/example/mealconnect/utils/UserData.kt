package com.example.mealconnect.utils

import LocationData
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class UserData(
    var id: String = "",
    var mealname: String = "",
    var image: String = "",
    var phoneno: String = "",
    var amount: String = "",
    var descp: String = "",
    var location: @RawValue LocationData = LocationData()
):Parcelable
