package com.example.mealconnect.fragments.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mealconnect.databinding.OrdersAdapterBinding
import com.example.mealconnect.utils.PartnerData

class OrdersAdapter:RecyclerView.Adapter<OrdersAdapter.OrderMyViewHolder>() {

    var orderlist:List<PartnerData> = emptyList()

    class OrderMyViewHolder(private val binding: OrdersAdapterBinding) :ViewHolder(binding.root){

        fun bindOrders(partnerData: PartnerData){
            binding.userdata=partnerData
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderMyViewHolder {

        return OrderMyViewHolder(OrdersAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: OrderMyViewHolder, position: Int) {

        val currentItem = orderlist[position]
        holder.bindOrders(currentItem)
    }

    override fun getItemCount(): Int {
      return orderlist.size
    }

    fun setOrders(orderList:List<PartnerData>){
        this.orderlist=orderList
        notifyDataSetChanged()
    }
}