package com.example.mealconnect.fragments.Adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.example.mealconnect.utils.PartnerData

class DiffCallback(private var oldlist:List<PartnerData>, private var newlist:List<PartnerData>):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldlist.size
    }

    override fun getNewListSize(): Int {
        return newlist.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return oldlist[oldItemPosition].id==newlist[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldlist[oldItemPosition]==newlist[newItemPosition]
    }
}