package com.example.mealconnect.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mealconnect.R
import com.example.mealconnect.databinding.FragmentDashboardBinding
import com.example.mealconnect.fragments.Adapter.MealAdapter
import com.example.mealconnect.utils.CustomProgressBar
import com.example.mealconnect.utils.NetworkResult
import com.example.mealconnect.utils.PartnerData
import com.example.mealconnect.utils.Permissions
import com.example.mealconnect.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class dashboard : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentDashboardBinding

    private val mainviewwmodel: MainViewModel by viewModels()
    private val adapter: MealAdapter by lazy { MealAdapter() }

    private lateinit var auth: FirebaseAuth
    private lateinit var databasereference: DatabaseReference
    private lateinit var newid: String

    private lateinit var progressBar: CustomProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        databasereference = FirebaseDatabase.getInstance().getReference("Partner").child(auth.currentUser.uid)

        newid = databasereference.push().key.toString()
    }


    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)

        progressBar = CustomProgressBar(requireContext(),null)
        binding.root.addView(progressBar)




        mainviewwmodel.getAllDataFromFirebase {userdata->
            when(userdata){
                is NetworkResult.Error -> {
                    progressBar.show()
                }
                is NetworkResult.Loading -> {
                    progressBar.show()
                }
                is NetworkResult.Success -> {
                    if (userdata.data!=null){
                        adapter.setdata(userdata.data)
                    }
                   progressBar.hide()
                }
            }
        }



        binding.actionbtntoadd.setOnClickListener {
            if (Permissions.isPermissionsGranted(requireContext())) {
                findNavController().navigate(R.id.action_dashboard_to_addmeal)
            } else {
                Permissions.requestPermissions(requireParentFragment())
            }
        }




        setUpRecyclerView()
        swipeToDelete(binding.recyclerview)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

    }

    private fun setUpRecyclerView() {
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {

        val swipeToDelete = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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

    private fun deleteItemFromFirebase(item: PartnerData?) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (item != null) {
                databasereference.child(item.key).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        showsnackbar(item)
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(), "Failed to Update:${item.mealname}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun showsnackbar(item: PartnerData?) {
        val snackbar =
            Snackbar.make(requireView(), "Deleted ${item?.mealname}", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            if (item != null) {
                addItemToFirebaseSnkbar(item)
            }
        }.show()
    }


    private fun addItemToFirebaseSnkbar(item:PartnerData) {
        lifecycleScope.launch(Dispatchers.Main) {

            val newuser = PartnerData(
                item.id,
                item.key,
                item.mealname,
                item.image,
                item.phoneno,
                item.amount,
                item.descp,
                item.partnerquantity,
                item.location,
                item.timestamp
            )
            databasereference.child(item.key).setValue(newuser).addOnSuccessListener {
                Toast.makeText(requireContext(), "Added ${item.mealname}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            Permissions.requestPermissions(requireParentFragment())
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        findNavController().navigate(R.id.action_dashboard_to_addmeal)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, requireContext()
        )
    }


}