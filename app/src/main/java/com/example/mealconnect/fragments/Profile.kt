package com.example.mealconnect.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.mealconnect.databinding.FragmentProfileBinding
import com.example.mealconnect.utils.PartnerData
import com.example.mealconnect.utils.UserData
import com.example.mealconnectuser.preferences.AppPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Profile : Fragment() {

    private var profile: FragmentProfileBinding?=null
    private val binding get() = profile!!

    private lateinit var auth:FirebaseAuth
    private lateinit var preferences: AppPreferences
    private lateinit var storageref:StorageReference
    private lateinit var id:String
    private lateinit var databaseref:DatabaseReference
    private  lateinit var imageuri:Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseref=FirebaseDatabase.getInstance().getReference("Users")
        auth= FirebaseAuth.getInstance()
        preferences = AppPreferences(requireContext())
        storageref=FirebaseStorage.getInstance().getReference("Images")
        id=databaseref.push().key.toString()
        setUserName()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profile= FragmentProfileBinding.inflate(layoutInflater,container,false)




        binding.savebtn.setOnClickListener {
            val name = binding.edttextname.text.toString()
            val email = binding.edttextemail.text.toString()
            val phoneno = binding.edttextphoneno.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && phoneno.isNotEmpty()){
                saveDetailsToFirebase(name, email, phoneno)
            }else{
                Toast.makeText(requireContext(),"Please Fill The Fields",Toast.LENGTH_SHORT).show()
            }
        }

        val pickimage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            binding.imageView.setImageURI(uri)
            if (uri != null) {
                imageuri = uri
            }
        }

        binding.imageView.setOnClickListener {
            pickimage.launch("image/*")
        }



        return binding.root
    }

    private fun saveDetailsToFirebase(name: String, email: String, phoneno: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (imageuri.toString().isNotEmpty()) {
                    val uploadTask = storageref.child(id).putFile(imageuri).await()
                    val imageUrl = uploadTask.metadata?.reference?.downloadUrl?.await().toString()

                    withContext(Dispatchers.Main) {
                        val databaseReference = databaseref.child(auth.currentUser.uid)
                        databaseReference.apply {
                            child("username").setValue(name).addOnCompleteListener { usernameTask ->
                                if (usernameTask.isSuccessful) {
                                    preferences.apply {
                                        this.email = email
                                        this.phoneno = phoneno
                                        username = name
                                        profileimage = imageUrl
                                    }.also {
                                        child("email").setValue(email)
                                        child("phoneno").setValue(phoneno)
                                        child("profileimage").setValue(imageUrl)
                                        intentToMainActivity()
                                    }
                                } else {
                                    Log.e(
                                        "Firebase",
                                        "Failed to save username: ${usernameTask.exception?.message}"
                                    )
                                }
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "No Image Found",
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
                    Log.e("Firebase Error", "Error uploading data to Firebase: ${e.message}", e)
                }
            }
        }
    }




    private fun intentToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }


    private fun setUserName(){
        databaseref.child(auth.currentUser.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user  = snapshot.getValue(UserData::class.java)
                binding.txtviewname.setText(user?.username)
                binding.edttextname.setText(user?.username)
                binding.edttextemail.setText(user?.email)
                binding.edttextphoneno.setText(user?.phoneno)
            }

            override fun onCancelled(error: DatabaseError) {
              Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
                Log.d("errormessagedatabase",error.message)
            }

        })
    }
}


