package com.example.mealconnect

import LocationData
import android.annotation.SuppressLint
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mealconnect.databinding.FragmentMapsBinding
import com.example.mealconnect.utils.CustomProgressBar
import com.example.mealconnect.utils.PartnerData
import com.example.mealconnect.viewmodel.MainViewModel
import com.example.mealconnect.viewmodel.ShareViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class MapsFragment : Fragment(),OnMapReadyCallback {

    private var mapfrag: FragmentMapsBinding? = null
    private val binding get() = mapfrag!!

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap

    private val args: MapsFragmentArgs by navArgs<MapsFragmentArgs>()
    private val sharedviewmodel: ShareViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseref: DatabaseReference
    private lateinit var key: String

    private lateinit var progressBar: CustomProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        databaseref = FirebaseDatabase.getInstance().getReference("Partner").child(auth.currentUser?.uid.orEmpty())
        key = databaseref.push().key.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mapfrag = FragmentMapsBinding.inflate(layoutInflater, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.mapfrag) as SupportMapFragment
        mapFragment.getMapAsync(this)

        progressBar = CustomProgressBar(requireContext(), null)
        binding.root.addView(progressBar)



        mainViewModel.currentLocation.observe(viewLifecycleOwner){
            if (it != null) {
                getCurrentLocationView(it)
            }
        }




        binding.finishbtn.setOnClickListener {
            val mealname = args.currentitem.mealname
            val imageUrl = args.currentitem.image
            val amount = args.currentitem.amount
            val phoneno = args.currentitem.phoneno
            val quantity = args.currentitem.partnerquantity
            val descp = args.currentitem.descp
            val isUpdated = args.isUpdated
            val location = LocationData(
                mainViewModel.currentLocation.value?.latitude ?: 0.0,
                mainViewModel.currentLocation.value?.longitude ?: 0.0
            )
            lifecycleScope.launch {
                progressBar.show()
                handleDataOperation(
                    mealname,
                    imageUrl,
                    phoneno,
                    amount,
                    descp,
                    quantity,
                    location,
                    isUpdated
                )
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_mapsFragment_to_addmeal)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.isMyLocationEnabled = true

    }


    private fun getCurrentLocationView(location:LatLng?) {
        val circleRadius = 100.0

        var currentLatLng = location?: LatLng(0.0, 0.0)

        val circleOptions = CircleOptions()
            .center(currentLatLng)
            .radius(circleRadius)
            .strokeColor(Color.YELLOW)
            .fillColor(Color.argb(70, 0, 0, 255))
            .strokeWidth(2f)

        map.clear()
        val circle = map.addCircle(circleOptions)
        map.setOnCameraMoveListener {
            currentLatLng = map.cameraPosition.target
            circle.center = currentLatLng
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16F))
    }


    private  fun handleDataInsertion(
        mealname: String,
        imageUrl: String,
        number: String,
        amount: String,
        descp: String,
        quantity: String,
        location: LocationData
    ) {
        val user = PartnerData(auth.currentUser.uid,key, mealname, imageUrl, number, amount, descp, quantity, location,mainViewModel.getCurrentTimestampMinutes())

        try {
            databaseref.child(key).setValue(user).addOnCompleteListener{
                if (it.isSuccessful){
                    databaseref.child(key).child("key").setValue(key)
                    databaseref.child(key).child("timestamp").setValue(mainViewModel.getCurrentTimestampMinutes())
                    Toast.makeText(
                        requireContext(),
                        "Added $mealname",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.hide()
                    findNavController().navigate(R.id.action_mapsFragment_to_dashboard)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Failed to add $mealname. Error: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("failureerror",e.message.toString())
        }

    }





    private  fun handleDataUpdation(
        mealname: String,
        imageUrl: String,
        number: String,
        amount: String,
        descp: String,
        quantity: String,
        location: LocationData
    ) {


        val user = mutableMapOf(
            "key" to args.currentitem.key,
            "mealname" to mealname,
            "image" to imageUrl,
            "phoneno" to number,
            "amount" to amount,
            "descp" to descp,
            "quantity" to quantity,
            "location" to location
        )

        try {
            databaseref.child(args.currentitem.key).updateChildren(user).addOnCompleteListener{
                if (it.isSuccessful){
                    databaseref.child(args.currentitem.key).child("key").setValue(args.currentitem.key)
                    databaseref.child(args.currentitem.key).child("id").setValue(auth.currentUser.uid)
                    Toast.makeText(
                        requireContext(),
                        "Updated $mealname",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.hide()
                    findNavController().navigate(R.id.action_mapsFragment_to_dashboard)
                }
            }

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Failed to Update $mealname. Error: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private  fun handleDataOperation(
        mealname: String,
        imageUrl: String,
        number: String,
        amount: String,
        descp: String,
        quantity: String,
        location: LocationData,
        isUpdate: Boolean
    ) {

            if (isUpdate) {
                handleDataUpdation(mealname, imageUrl, number, amount, descp, quantity, location)
            } else {
                handleDataInsertion(mealname, imageUrl, number, amount, descp, quantity, location)
            }


    }
}
