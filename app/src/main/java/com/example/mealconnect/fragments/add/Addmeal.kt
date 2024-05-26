package com.example.mealconnect.fragments.add

import LocationData
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mealconnect.R
import com.example.mealconnect.databinding.FragmentAddmealBinding
import com.example.mealconnect.notifications.entity.NofiticationData
import com.example.mealconnect.notifications.entity.PushNotification
import com.example.mealconnect.utils.CustomProgressBar
import com.example.mealconnect.utils.PartnerData
import com.example.mealconnect.viewmodel.MainViewModel
import com.example.mealconnect.viewmodel.ShareViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class addmeal : Fragment(){

    private var addfrag: FragmentAddmealBinding? = null
    private val binding get() = addfrag!!

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseref: DatabaseReference
    private lateinit var storageref: StorageReference
    private lateinit var key: String

    private val sharedviewmodel: ShareViewModel by viewModels()
    private var imageuri: Uri? = null

    private var fcm: FirebaseMessaging? = null

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var progressBar: CustomProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        databaseref = FirebaseDatabase.getInstance().getReference("Partner").child(auth.currentUser?.uid.orEmpty())
        key = databaseref.push().key.toString()
        storageref = FirebaseStorage.getInstance().getReference("Images")
        fcm = FirebaseMessaging.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addfrag = FragmentAddmealBinding.inflate(layoutInflater, container, false)

        progressBar = CustomProgressBar(requireContext(), null)
        binding.root.addView(progressBar)



        binding.Nextbtn.setOnClickListener {
            progressBar.show()
            insertDataIntoFirebase()
        }

        binding.cancelbtn.setOnClickListener {
            findNavController().navigate(R.id.action_addmeal_to_dashboard)
            progressBar.hide()
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
            findNavController().navigate(R.id.action_addmeal_to_dashboard)
        }
    }

    private fun insertDataIntoFirebase() {
        val mealname = binding.titletxt.editText?.text.toString()
        val number = binding.edttxtphoneno.editText?.text.toString()
        val amount = binding.edttxtamount.editText?.text.toString()
        val descp = binding.edttxtdescp.editText?.text.toString()
        val quantity = binding.edttxtquantity.editText?.text.toString()
        val location = LocationData(0.0, 0.0)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                imageuri?.let {

                    val uploadTask = storageref.child(key).putFile(it).await()
                    val imageUrl = uploadTask.metadata?.reference?.downloadUrl?.await().toString()

                    withContext(Dispatchers.Main) {
                        if (sharedviewmodel.UserDataValidation(mealname, number, amount, descp, imageUrl, location, quantity)) {
                            progressBar.setText("Loading Maps,Please wait...")
                            val currentitem = PartnerData(auth.currentUser.uid, key, mealname, imageUrl, number, amount, descp, quantity, location, mainViewModel.getCurrentTimestampMinutes())
                            val details = addmealDirections.actionAddmealToMapsFragment(currentitem, false)
                            findNavController().navigate(details)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please Fill The Required Fields",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressBar.hide()
                        }
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "No Image Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    progressBar.hide()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                progressBar.hide()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun sendNotification(mealname: String, amount: String) {
        databaseref.child("Tokens").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (token in snapshot.children) {
                    Log.d("tokenbypass", token.toString())
                    mainViewModel.sendNotification(
                        PushNotification(
                            NofiticationData("Added $mealname", "Added $mealname of Rs $amount.Please checkout...."),
                            token.toString()
                        )
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addfrag = null
    }
}
