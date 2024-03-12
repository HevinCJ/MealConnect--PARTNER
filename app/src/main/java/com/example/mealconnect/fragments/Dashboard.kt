package com.example.mealconnect.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Path.Direction
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.motion.widget.OnSwipe
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mealconnect.R
import com.example.mealconnect.databinding.FragmentDashboardBinding
import com.example.mealconnect.fragments.Adapter.MealAdapter
import com.example.mealconnect.utils.Permissions
import com.example.mealconnect.utils.UserData
import com.example.mealconnect.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class dashboard : Fragment(),EasyPermissions.PermissionCallbacks {

    private lateinit var binding:FragmentDashboardBinding

    private val mainviewwmodel:MainViewModel by viewModels()
    private val adapter:MealAdapter by lazy { MealAdapter() }

    private var databasereference:DatabaseReference?=null
    private lateinit var newid:String



    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        binding = FragmentDashboardBinding.inflate(layoutInflater,container,false)
        databasereference = FirebaseDatabase.getInstance().getReference("Users")
        newid= databasereference?.push()?.key.toString()




        mainviewwmodel.getAllData.observe(viewLifecycleOwner){userdata->
            if (userdata!=null){
                adapter.setdata(userdata)
                Log.d("userdataget",userdata.toString())
            }
        }



        binding.actionbtntoadd.setOnClickListener {
                if (Permissions.isPermissionsGranted(requireContext())){
                    findNavController().navigate(R.id.action_dashboard_to_addmeal)
                }else{
                    Permissions.requestPermissions(requireParentFragment())
                }
        }




        setUpRecyclerView()
        swipeToDelete(binding.recyclerview)



     return binding.root
    }

    private fun setUpRecyclerView() {
        binding.recyclerview.adapter=adapter
        binding.recyclerview.layoutManager=LinearLayoutManager(requireActivity())
    }

    private fun swipeToDelete(recyclerView: RecyclerView){

         val swipeToDelete = object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.datalist[viewHolder.adapterPosition]
                deleteItemFromFirebase(item)
            }

        }

        val itemtouchhelper = ItemTouchHelper(swipeToDelete)
        itemtouchhelper.attachToRecyclerView(recyclerView)

    }

    private fun deleteItemFromFirebase(item:UserData?) {
        lifecycleScope.launch(Dispatchers.IO){
            if (item!=null){
                databasereference?.child(item.id)?.removeValue()?.addOnCompleteListener {
                    if (it.isSuccessful){
                        showsnackbar(item)
                    }
                }?.addOnFailureListener {
                    Toast.makeText(requireContext(),"Failed to Updata:${item.mealname}",Toast.LENGTH_SHORT).show()
                }
            }
            }

    }

    private fun showsnackbar(item: UserData?) {
        val snackbar = Snackbar.make(requireView(), "Deleted ${item?.mealname}", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            if (item!=null){
                addItemToFirebaseSnkbar(item)
            }
            }.show()
        }



    private fun addItemToFirebaseSnkbar(item: UserData) {
        lifecycleScope.launch(Dispatchers.Main) {
            val newId = databasereference?.push()?.key.toString()

            val newuser = UserData(
                item.id,
                item.mealname,
                item.image,
                item.phoneno,
                item.amount,
                item.descp,
                item.location
            )
            databasereference?.child(item.id)?.setValue(newuser)?.addOnSuccessListener{
                Toast.makeText(requireContext(), "Added ${item.mealname}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            SettingsDialog.Builder(requireActivity())
                .build()
                .show()
        } else {
            Permissions.requestPermissions(requireParentFragment())
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        findNavController().navigate(R.id.action_dashboard_to_addmeal)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,requireContext())
    }





}