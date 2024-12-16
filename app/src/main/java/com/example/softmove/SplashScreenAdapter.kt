package com.example.softmove

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.softmove.Models.Splashscreenitems
import com.example.softmove.SplashScreenAdapter.*
import com.example.softmove.databinding.SliderItemBinding
import com.github.islamkhsh.CardSliderAdapter
//import kotlinx.android.synthetic.main.slider_item.view.*

class SplashScreenAdapter(private val splashitems: List<Splashscreenitems>) : CardSliderAdapter<SplashViewHolder>() {

    lateinit var sliderItemBinding: SliderItemBinding

    override fun bindVH(holder: SplashViewHolder, position: Int) {

        val splashscreenitem = splashitems[position]

        holder.itemView.run {
            sliderItemBinding.sliderImg.setImageResource(splashscreenitem.sliderimg)
            sliderItemBinding.sliderTitle.text = splashscreenitem.slidertitle
            sliderItemBinding.sliderDesc.text = splashscreenitem.sliderdesc
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SplashViewHolder {
        sliderItemBinding = SliderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val view = sliderItemBinding.root
        return SplashViewHolder(view)

    }

    override fun getItemCount() = splashitems.size


    class SplashViewHolder(view: View) : RecyclerView.ViewHolder(view)
}