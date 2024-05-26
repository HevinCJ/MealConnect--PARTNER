package com.example.mealconnect.fragments.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mealconnect.databinding.MealAdapterBinding
import com.example.mealconnect.fragments.Adapter.diffutil.DiffCallback
import com.example.mealconnect.utils.PartnerData

class MealAdapter:RecyclerView.Adapter<MealAdapter.mealViewHolder>() {

     var datalist:List<PartnerData> = emptyList()

    class mealViewHolder(private val binding:MealAdapterBinding): ViewHolder(binding.root) {

        fun bindmeal(userData: PartnerData){
               binding.userdata = userData
                binding.executePendingBindings()
                Log.d("datalist",userData.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mealViewHolder {
        return mealViewHolder(MealAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: mealViewHolder, position: Int) {
      val currentmeal = datalist[position]
        holder.bindmeal(currentmeal)
        Log.d("currentmeal",currentmeal.toString())
    }


    fun setdata(userData: List<PartnerData>) {
        val diffCallback = DiffCallback(datalist, userData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        datalist=userData
        diffResult.dispatchUpdatesTo(this)
    }




}