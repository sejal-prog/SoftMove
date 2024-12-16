package com.example.softmove.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.softmove.Models.Exercises
import com.example.softmove.databinding.ExcerciseCardBinding

class ExerciseAdapter(private val exercises: List<Exercises>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private var onItemClickListener: ((Exercises) -> Unit)? = null

    fun setOnItemClickListener(listener: (Exercises) -> Unit) {
        onItemClickListener = listener
    }

    inner class ExerciseViewHolder(val binding: ExcerciseCardBinding) : RecyclerView.ViewHolder(binding.root)
//    {
//        fun bind(exercise: Exercises) {
//           // binding.ExcercisecardSrno.text = (absoluteAdapterPosition + 1).toString()
//            binding.ExcercisecardName.text = exercise.Excercise_name
//            binding.ExcercisecardTime.text = exercise.Excercise_time
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ExcerciseCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        with(holder){
            with(exercises[position]){
                binding.ExcercisecardSrno.text = (absoluteAdapterPosition + 1).toString()
                binding.ExcercisecardName.text = this.Exercise_name
                binding.ExcercisecardTime.text = this.Exercise_time

                itemView.setOnClickListener {
                    onItemClickListener?.let { listener ->
                        listener(exercise)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }
}

