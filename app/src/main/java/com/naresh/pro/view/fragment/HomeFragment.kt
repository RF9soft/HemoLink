package com.naresh.pro.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.naresh.pro.databinding.FragmentHomeBinding
import com.naresh.pro.utils.Constants
import com.naresh.pro.utils.DataState
import com.naresh.pro.view.adapter.PostAdapter
import com.naresh.pro.viewmodel.PostViewModel

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val viewModel : PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        binding.recyclerView.layoutManager = Constants.getVerticalLayoutManager(requireContext())
        viewModel.getBloodPosts()

        viewModel.postLiveData.observe(viewLifecycleOwner){
            when (it){
                is DataState.Loading -> {}

                is DataState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val adapter = PostAdapter(it.data!!, requireContext())

                    if (adapter.itemCount > 0){
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.noDataLayout.visibility = View.GONE
                        binding.recyclerView.adapter = adapter
                    }
                    else {
                        binding.recyclerView.visibility = View.GONE
                        binding.noDataLayout.visibility = View.VISIBLE
                    }
                }

                is DataState.Failed -> {
                    binding.progressBar.visibility = View.GONE
                    binding.noDataLayout.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
            }
        }

        return binding.root
    }
}