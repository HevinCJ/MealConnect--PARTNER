package com.example.mealconnect.fragments.Adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.example.mealconnect.utils.UserData

class DiffCallback(private var oldlist:List<UserData>,private var newlist:List<UserData>):DiffUtil.Callback() {
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