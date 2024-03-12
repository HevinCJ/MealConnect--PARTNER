package com.example.mealconnect.fragments.Adapter

import android.text.Layout.Directions
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.mealconnect.R
import com.example.mealconnect.UpdataMealDirections
import com.example.mealconnect.fragments.dashboardDirections
import com.example.mealconnect.utils.UserData

class BindingAdapter {

    companion object{

        @BindingAdapter("android:convertlinktoimage")
        @JvmStatic
        fun convertLinkToImage(imageView: ImageView,imageurl:String){
            Glide.with(imageView.context)
                .load(imageurl)
                .centerCrop()
                .into(imageView)
        }

        @BindingAdapter("android:adapterToUpdateFragment")
        @JvmStatic

        fun adapterToUpdateFragment(view:ConstraintLayout,userData: UserData){
            view.setOnClickListener {
                val action =dashboardDirections.actionDashboardToUpdataMeal(userData)
                view.findNavController().navigate(action)
            }
        }



    }

}