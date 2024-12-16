package com.example.softmove

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.softmove.Views.Login
import com.example.softmove.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding= FragmentProfileBinding.inflate(inflater, container, false)

        binding.logout.setOnClickListener{
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
        }

        binding.btnAbout.setOnClickListener{
            val intent=Intent(requireContext(), About::class.java)
            startActivity(intent)
        }
        binding.btnChangePass.setOnClickListener {
            val intent=Intent(requireContext(), Forgotpassword::class.java)
            startActivity(intent)
        }

        return binding.root;
    }


}