package com.example.mealconnect.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.mealconnect.R
import com.example.mealconnect.StartActivity
import com.example.mealconnect.databinding.FragmentLoginBinding
import com.example.mealconnectuser.preferences.AppPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class login : Fragment() {
    private var login:FragmentLoginBinding?=null
    val binding get() = login!!

    private  lateinit var auth:FirebaseAuth
    private  var databaseref:DatabaseReference ?=null
    private lateinit var preferences:AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= FirebaseAuth.getInstance()
        databaseref=FirebaseDatabase.getInstance().getReference("Users")
        preferences = AppPreferences(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        login = FragmentLoginBinding.inflate(layoutInflater,container,false)


        if (auth.currentUser!=null){
           IntentToMainActivity()
        }

        binding.txtviewsignup.setOnClickListener{
            findNavController().navigate(R.id.action_login_to_signup2)
        }


        binding.loginbutton.setOnClickListener {
            loginUserToFirebase()
        }

        return binding.root
    }



    private fun loginUserToFirebase() {
        val email = binding.edttextemail.text.toString()
        val password = binding.edttextpassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                if (it.isSuccessful){
                    IntentToMainActivity()
                    Toast.makeText(requireContext(),"Logged In",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(),it.exception.toString(),Toast.LENGTH_SHORT).show()
                    Log.d("exceptionMessage",it.exception.toString())
                }
            }
        }else{
            Toast.makeText(requireContext(),"Please fill fields",Toast.LENGTH_SHORT).show()
        }

    }



    private fun IntentToMainActivity() {
        if (preferences.profileimage.isNullOrEmpty() || preferences.phoneno.isNullOrEmpty() || preferences.email.isNullOrEmpty()||preferences.username.isNullOrEmpty()){
            findNavController().navigate(R.id.action_login_to_profile3)
        }else{
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        login=null
    }
}