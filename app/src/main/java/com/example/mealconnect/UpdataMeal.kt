package com.example.mealconnect

import LocationData
import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mealconnect.databinding.FragmentUpdataMealBinding
import com.example.mealconnect.utils.UserData
import com.example.mealconnect.viewmodel.ShareViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
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

class UpdataMeal : Fragment(),OnMapReadyCallback {
    private var updatemeal: FragmentUpdataMealBinding? = null
    private val binding get() = updatemeal!!

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseref: DatabaseReference
    private lateinit var storageref: StorageReference
    private lateinit var id: String

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
        databaseref = FirebaseDatabase.getInstance().getReference("Users")
        id = databaseref.push().key.toString()
        storageref = FirebaseStorage.getInstance().getReference("Images")

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        mapFragment = childFragmentManager.findFragmentById(R.id.mapview2) as SupportMapFragment
        mapFragment?.getMapAsync(this)

        showPreviewData()

        binding.updatemealbtn.setOnClickListener {
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

    private fun showPreviewData() {
        binding.titletxt.editText?.setText(args.CurrentMeal.mealname)
        binding.edttxtphoneno.editText?.setText(args.CurrentMeal.phoneno)
        binding.edttxtamount.editText?.setText(args.CurrentMeal.amount)
        binding.edttxtdescp.editText?.setText(args.CurrentMeal.descp)


        Glide.with(requireContext())
            .load(args.CurrentMeal.image)
            .into(binding.imageButton)

    }

    private fun updateDataIntoFirebase() {
        val mealname = binding.titletxt.editText?.text.toString()
        val number = binding.edttxtphoneno.editText?.text.toString()
        val amount = binding.edttxtamount.editText?.text.toString()
        val descp = binding.edttxtdescp.editText?.text.toString()
        val location = LocationData(
            currentLocation.value?.latitude ?: 0.0,
            currentLocation.value?.longitude ?: 0.0
        )

        CoroutineScope(Dispatchers.IO).launch {
            binding.updatemealbtn.setText(getString(R.string.uploading_image_text))
            try {
                imageuri?.let {
                    val uploadTask = storageref.child(id).putFile(it).await()

                    val imageUrl = uploadTask.metadata?.reference?.downloadUrl?.await().toString()
                    binding.updatemealbtn.setText(getString(R.string.updating_please_wait_text))

                    withContext(Dispatchers.Main) {
                        handleDataInsertion(mealname, imageUrl, number, amount, descp, location)
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Image is null. Please select an image.",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.updatemealbtn.setText(getString(R.string.upload_meal_text))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.updatemealbtn.setText(getString(R.string.upload_meal_text))
                }
            }
        }

    }

    private suspend fun handleDataInsertion(
        mealname: String,
        imageUrl: String,
        number: String,
        amount: String,
        descp: String,
        location: LocationData
    ) {
        if (sharedviewmodel.UserDataValidation(
                mealname,
                number,
                amount,
                descp,
                imageUrl,
                location
            )
        ) {

            val user = mutableMapOf<String,Any>(
                "id" to args.CurrentMeal.id,
                "mealname" to  mealname,
                "image" to imageUrl,
                "phoneno" to number,
                "amount" to amount,
                "descp" to descp,
                "location" to location


            )

            try {
                databaseref.child(args.CurrentMeal.id).updateChildren(user).await()

                Toast.makeText(
                    requireContext(),
                    "Updated $mealname",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(R.id.action_updataMeal_to_dashboard)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Failed to Update $mealname. Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Please Fill the Fields",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map?.mapType = GoogleMap.MAP_TYPE_NORMAL
        map?.isMyLocationEnabled = true
        getLastLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient?.lastLocation?.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val location: Location? = task.result
                if (location != null) {
                    currentLocation.value = LatLng(location.latitude, location.longitude)
                    getCurrentLocationView(currentLocation)
                } else {
                    requestNewLocationData()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        updatemeal = null
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
            .setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            if (mLastLocation != null) {
                currentLocation.value = LatLng(mLastLocation.latitude, mLastLocation.longitude)
                getCurrentLocationView(currentLocation)
            }
        }
    }
    private fun getCurrentLocationView(location: MutableLiveData<LatLng>?) {
        var currentLocation = LatLng(location?.value!!.latitude, location.value!!.longitude)

        val circleRadius = 100.0

        val circleOptions = CircleOptions()
            .center(currentLocation)
            .radius(circleRadius)
            .strokeColor(Color.YELLOW)
            .fillColor(Color.argb(70, 0, 0, 255))
            .strokeWidth(2f)

        map?.clear()
        val circle = map?.addCircle(circleOptions)
        map?.setOnCameraMoveListener {
            currentLocation = map?.cameraPosition?.target ?: currentLocation
            circle?.center = currentLocation
        }
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16F))
    }
}