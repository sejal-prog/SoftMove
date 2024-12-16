package com.example.softmove

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.softmove.R
import com.example.softmove.databinding.ActivityLoginBinding
import com.example.softmove.databinding.FragmentDiscoverBinding
import java.io.ByteArrayOutputStream

class DiscoverFragment : Fragment() {
    private var _binding: FragmentDiscoverBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.discoverfragYogaexcercise.setOnClickListener(){
            val text: String = binding.myImageViewText.getText().toString()
            val bitmap = (binding.menuYoga.getDrawable() as BitmapDrawable).bitmap
            callIntent(text,bitmap)
        }

        binding.discoverfragStrechingexcercise.setOnClickListener{
            val text: String = binding.streching.getText().toString()
            val bitmap = (binding.menuStreching.getDrawable() as BitmapDrawable).bitmap
            callIntent(text,bitmap)
        }

//        binding.discoverfragCyclingexcercise.setOnClickListener{
//            val text: String = binding.cycling.getText().toString()
//            val bitmap = (binding.menuCycling.getDrawable() as BitmapDrawable).bitmap
//            callIntent(text,bitmap)
//        }

//        binding.discoverfragRunningexcercise.setOnClickListener{
//            val text: String = binding.running.getText().toString()
//            val bitmap = (binding.menuRunning.getDrawable() as BitmapDrawable).bitmap
//            callIntent(text,bitmap)
//        }

        binding.discoverfragMeditationexcercise.setOnClickListener{
//            val text: String = binding.guidedMeditation.getText().toString()
//            val bitmap = (binding.menuGuidedmeditation.getDrawable() as BitmapDrawable).bitmap
//            callIntent(text,bitmap)
            val intent=Intent(activity, Mediatationbreathing::class.java)
            startActivity(intent)
        }



        return view
    }

    fun callIntent(text:String,bitmap: Bitmap){
        val intent = Intent(requireActivity(), Excercises_screen::class.java)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val compressedImageBytes: ByteArray = outputStream.toByteArray()
        intent.putExtra("text", text)
        intent.putExtra("image", compressedImageBytes)
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}