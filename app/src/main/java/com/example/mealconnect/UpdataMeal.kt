package com.example.mealconnect

import LocationData
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mealconnect.databinding.FragmentUpdataMealBinding
import com.example.mealconnect.utils.PartnerData
import com.example.mealconnect.viewmodel.MainViewModel
import com.example.mealconnect.viewmodel.ShareViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UpdataMeal : Fragment() {
    private var updatemeal: FragmentUpdataMealBinding? = null
    private val binding get() = updatemeal!!

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseref: DatabaseReference
    private lateinit var storageref: StorageReference
    private lateinit var key: String

    private val mainViewModel:MainViewModel by viewModels()

    private var mapFragment: SupportMapFragment? = null
    private var map: GoogleMap? = null

    private val sharedviewmodel: ShareViewModel by viewModels()
    private var imageuri: Uri? = null

    private var currentLocation = MutableLiveData<LatLng>()
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private val args by navArgs<UpdataMealArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        updatemeal = FragmentUpdataMealBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()
        databaseref = FirebaseDatabase.getInstance().getReference("Partner").child(auth.currentUser?.uid.orEmpty())
        key = databaseref.push().key.toString()
        storageref = FirebaseStorage.getInstance().getReference("Images")

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        showPreviewData()

        binding.Nextbtn.setOnClickListener {
            updateDataIntoFirebase()
        }


        val pickimage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            binding.imageButton.setImageURI(uri)
            if (uri != null) {
                imageuri = uri
            }
        }

        binding.imageButton.setOnClickListener {
            pickimage.launch("image/*")
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            findNavController().navigate(R.id.action_updataMeal_to_dashboard)
        }
    }

    private fun showPreviewData() {
        binding.titletxt.editText?.setText(args.CurrentMeal.mealname)
        binding.edttxtphoneno.editText?.setText(args.CurrentMeal.phoneno)
        binding.edttxtamount.editText?.setText(args.CurrentMeal.amount)
        binding.edttxtdescp.editText?.setText(args.CurrentMeal.descp)
        binding.edttxtquantity.editText?.setText(args.CurrentMeal.partnerquantity)


        Glide.with(requireContext())
            .load(args.CurrentMeal.image)
            .into(binding.imageButton)

    }

    private fun updateDataIntoFirebase() {
        val mealname = binding.titletxt.editText?.text.toString()
        val number = binding.edttxtphoneno.editText?.text.toString()
        val amount = binding.edttxtamount.editText?.text.toString()
        val descp = binding.edttxtdescp.editText?.text.toString()
        val quantity  = binding.edttxtquantity.editText?.text.toString()
        val location = LocationData(
            currentLocation.value?.latitude ?: 0.0,
            currentLocation.value?.longitude ?: 0.0
        )

        CoroutineScope(Dispatchers.IO).launch {

            try {
                imageuri?.let {
                    val uploadTask = storageref.child(key).putFile(it).await()
                    val imageUrl = uploadTask.metadata?.reference?.downloadUrl?.await().toString()

                    withContext(Dispatchers.Main) {
                        if (sharedviewmodel.UserDataValidation(mealname,number,amount,descp,imageUrl,location,quantity)){
                       val currentItem = PartnerData(args.CurrentMeal.id,args.CurrentMeal.key,mealname,imageUrl,number,amount,descp,quantity,location,mainViewModel.getCurrentTimestampMinutes())
                        val details = UpdataMealDirections.actionUpdataMealToMapsFragment(currentItem,true)
                        findNavController().navigate(details)
                        }else{
                            Toast.makeText(
                                requireContext(),
                                "Please Fill The Required Fields",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                 }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "No Image Found. Please select an image.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }

    }





    override fun onDestroyView() {
        super.onDestroyView()
        updatemeal = null
    }



}