package com.example.softmove

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import com.example.softmove.ViewModel.ExercisesViewModel
import com.example.softmove.ViewModel.HomeViewModel
import com.example.softmove.databinding.FragmentDashboardBinding
import com.example.softmove.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class DashboardFragment : Fragment() {

    private lateinit var binding:FragmentDashboardBinding
    private lateinit var viewModel: ExercisesViewModel
    lateinit var currentUser: FirebaseUser
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var lastdatesharedpreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_dashboard, container, false)
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        currentUser = FirebaseAuth.getInstance().currentUser!!
        val uid = FirebaseAuth.getInstance().uid

        viewModel = ViewModelProvider(this).get(ExercisesViewModel::class.java)
        viewModel.init(requireContext(), uid!!)


        updateReportTexts()
        updateReportProgress()
        //checkStreakCount()
        // streak
        onAttach(requireContext())
        //editshared()
        updateStreakUI(requireContext())


        return binding.root
    }

//    private fun editshared() {
//
//        val editor = lastdatesharedpreferences.edit()
//        val dateToSave = "2023-05-17" // Replace with your desired date
//        editor.putString("lastUpdatedDate", dateToSave)
//        editor.apply()
//    }

//    fun checkStreakCount(){
//        val streakcount = sharedPreferences.getInt("StreakCount", 0)
//        if(streakcount<=0){
//            binding.streakcard.visibility=View.GONE
//        }else{
//            binding.streakcard.visibility=View.VISIBLE
//        }
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = requireContext().getSharedPreferences("StreakCount", Context.MODE_PRIVATE)
        lastdatesharedpreferences = requireContext().getSharedPreferences("lastUpdatedDate", Context.MODE_PRIVATE)
    }



    fun updateStreakUI(context:Context) {

        // Get the current date and the stored last updated date from SharedPreferences
        val currentDate = LocalDate.now()
        //Log.i("Current Date",currentDate.toString())
        val lastUpdatedDate = getLastUpdatedDateFromSharedPreferences() // Replace with your own method to retrieve the stored date

        // Check if the current date and last updated date are consecutive
        if (isConsecutiveDates(currentDate, lastUpdatedDate)) {
            // Update the streak count and display it in the UI

            val streakCount = getUpdatedStreakCount() // Replace with your own method to calculate and update the streak count
            binding.streakcount.text = "Streak:"+ streakCount.toString()+" Days"
            //val quote = getRandomQuote(context)

            // Display the quote in your UI or send it as a notification
           /// homeBinding.quote.text=quote

            // Update the last updated date in SharedPreferences
            saveLastUpdatedDateToSharedPreferences(currentDate) // Replace with your own method to store the current date
        } else {

            // Reset the streak count to 0 and display it in the UI
            resetStreakCount()
            binding.streakcount.text = "0"
        }
    }

    fun isConsecutiveDates(currentDate: LocalDate, lastUpdatedDate: LocalDate): Boolean {
        return currentDate.minusDays(1) == lastUpdatedDate
    }

    fun getLastUpdatedDateFromSharedPreferences(): LocalDate {
        // Retrieve the last updated date from SharedPreferences and convert it to LocalDate
        val dateString = lastdatesharedpreferences.getString("lastUpdatedDate", null)
        return if (dateString != null) {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
        } else {
            // Return a default date if the value is not found in SharedPreferences
            LocalDate.MIN
        }
    }

    fun getUpdatedStreakCount(): Int {
        // Calculate and return the updated streak count based on your logic
        // For example, you could retrieve the previous streak count from SharedPreferences and increment it
        val previousStreakCount = sharedPreferences.getInt("StreakCount", 0)
        return previousStreakCount + 1
    }

    fun saveLastUpdatedDateToSharedPreferences(date: LocalDate) {
        // Convert the LocalDate to a string format and store it in SharedPreferences
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        sharedPreferences.edit {
            putString("lastUpdatedDate", dateString)
        }
    }

    fun resetStreakCount() {
        // Reset the streak count in SharedPreferences to 0
        sharedPreferences.edit {
            putInt("StreakCount", 0)
        }
    }


    private fun updateReportTexts() {

        binding.valYogaTimes.text = viewModel.getYogaCount().toString()
        binding.valYoga.text = viewModel.getYogaSec().toString()
        binding.valStretchingTimes.text = viewModel.getStretchingCount().toString()
        binding.valStretching.text = viewModel.getStretchingSec().toString()
    }
    private fun updateReportProgress() {
        val yogaMin = viewModel.getYogaSec()
        val stretchingMin = viewModel.getStretchingSec()
        val yogaCount = viewModel.getYogaCount()
        val stretchingCount = viewModel.getStretchingCount()

        binding.valYogaTimes.text = yogaCount.toString()
        binding.valYoga.text  = yogaMin.toString()
        binding.valStretchingTimes.text = stretchingCount.toString()
        binding.valStretching.text = stretchingMin.toString()
    }


}