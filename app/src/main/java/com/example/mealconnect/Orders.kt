package com.example.mealconnect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealconnect.databinding.FragmentOrdersBinding
import com.example.mealconnect.fragments.Adapter.OrdersAdapter
import com.example.mealconnect.utils.CustomProgressBar
import com.example.mealconnect.utils.NetworkResult
import com.example.mealconnect.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class Orders : Fragment() {
    private var orders: FragmentOrdersBinding? =null
    private val binding get() = orders!!

    private val adapter by lazy { OrdersAdapter() }

    private val mainViewModel:MainViewModel by viewModels()

    private lateinit var progressBar: CustomProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        orders = FragmentOrdersBinding.inflate(layoutInflater,container,false)
        setUpRecyclerView()

        progressBar = CustomProgressBar(requireContext(),null)
        binding.root.addView(progressBar)

        mainViewModel.getAllOrdersFromFirebase {orderlist->
            when(orderlist){
                is NetworkResult.Error ->{
                    Toast.makeText(
                        requireContext(), orderlist.message, Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                  progressBar.show()
                }
                is NetworkResult.Success -> {
                    orderlist.data?.let { adapter.setOrders(it) }
                    progressBar.hide()
                }
            }
        }

        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.recyclerviewOrders.adapter = adapter
        binding.recyclerviewOrders.layoutManager = LinearLayoutManager(requireActivity())
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
           findNavController().navigate(R.id.action_orders_to_dashboard)

            val bottonnavview = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottonnavview.selectedItemId=R.id.Homem
        }


    }
}