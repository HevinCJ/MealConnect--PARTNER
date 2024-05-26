package com.example.mealconnect.fragments.Adapter

import android.content.Intent
import android.net.Uri
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.mealconnect.R
import com.example.mealconnect.fragments.dashboardDirections
import com.example.mealconnect.utils.PartnerData

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

        fun adapterToUpdateFragment(view:ConstraintLayout,userData: PartnerData){
            view.setOnClickListener {
                val action =dashboardDirections.actionDashboardToUpdataMeal(userData)
                view.findNavController().navigate(action)
            }
        }

        @BindingAdapter("android:convertUrlToImage")
        @JvmStatic
        fun convertUrlToImage(view:ImageView,url:String){
            Glide.with(view.context)
                .load(url)
                .centerCrop()
                .into(view)
        }

        @JvmStatic
        fun checkWhetherZeroOrNot(textView: TextView, amount:String){
            val newAmount = amount.toInt()
            if (newAmount == 0) {
                textView.setText(("Free"))
            }else{
                textView.text = "$amount Rs"
            }

        }


        @BindingAdapter("android:IntentToCallerApp")
        @JvmStatic
        fun IntentToCallerApp(view: ImageButton, phoneno:String){
            view.setOnClickListener {
                if (phoneno.isNotEmpty()) {
                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneno"))
                    view.context.startActivity(callIntent)
                }
            }
        }
    }

}