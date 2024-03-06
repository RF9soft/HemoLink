package com.naresh.pro.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.naresh.pro.R
import com.naresh.pro.databinding.FragmentAuthBinding
import com.naresh.pro.utils.AlertDialog
import com.naresh.pro.utils.Constants
import com.naresh.pro.utils.DataState
import com.naresh.pro.viewmodel.AuthViewModel

class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding
    private val viewModel: AuthViewModel by viewModels()
    private var isButtonEnabled = false
    private var verificationID = ""
    private var countryCode = "+91"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater)
        val countryCodeEditText = binding.countryCodeEditText

        makeButtonEnabled()

        binding.authBtn.setOnClickListener {
            if (isButtonEnabled) {
                viewModel.authLiveData.removeObservers(viewLifecycleOwner)
                if (Constants.internetAvailable(requireContext())) {
                    // Send verification code with the formatted phone number
                    viewModel.sendVerificationCode(
                        requireActivity(),
                        formatPhoneNumberToE164(binding.number.text.toString())
                    )

                    viewModel.authLiveData.observe(viewLifecycleOwner) {
                        when (it) {
                            is DataState.Loading -> {
                                binding.btnTxt.visibility = View.GONE
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is DataState.Success -> {
                                verificationID = it.data!!
                                val array = arrayOf(verificationID, "${binding.number.text}")
                                val action = AuthFragmentDirections.authToVerification(array)
                                Navigation.findNavController(binding.root).navigate(action)
                                binding.progressBar.visibility = View.GONE
                                binding.btnTxt.visibility = View.VISIBLE
                            }

                            is DataState.Failed -> {
                                AlertDialog.showAlertDialog(
                                    requireContext(),
                                    it.message!!,
                                    "Close"
                                )
                                binding.progressBar.visibility = View.GONE
                                binding.btnTxt.visibility = View.VISIBLE
                            }
                        }
                    }
                } else {
                    AlertDialog.showAlertDialog(
                        requireContext(),
                        "No internet connection available. Please check your internet connection and try again",
                        "Close"
                    )
                }
            }
        }

        return binding.root
    }

    private fun makeButtonEnabled() {
        binding.number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //Before
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //On
            }

            override fun afterTextChanged(s: Editable?) {
                val enteredNumber = s.toString()

                if (enteredNumber.length < 10) {
                    isButtonEnabled = false
                    binding.authBtn.setBackgroundResource(R.drawable.bg_disabled_button)
                } else {
                    isButtonEnabled = true
                    binding.authBtn.setBackgroundResource(R.drawable.bg_light_red_5)
                }

                // Extract country code from the entered number
                if (enteredNumber.length >= 3) {
                    countryCode = enteredNumber.substring(0, 3) // Assuming the country code length is 2
                }
            }
        })
    }

    // Function to format the phone number to E.164 format
    private fun formatPhoneNumberToE164(phoneNumber: String): String {
        // You may need to adjust this function based on your requirements
        return countryCode + phoneNumber.substring(3) // Assuming the country code length is 2
    }
}
