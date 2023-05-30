package com.dicoding.sub1_appsstory.Adapter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.sub1_appsstory.Data.ListStoryItem
import com.dicoding.sub1_appsstory.Detail.DetailStoryActivity
import com.dicoding.sub1_appsstory.Utils.DateConverter
import com.dicoding.sub1_appsstory.databinding.StoryItemBinding

class AdapterStory(private val listStory: List<ListStoryItem>) :
    RecyclerView.Adapter<AdapterStory.ViewHolder>() {

    class ViewHolder(var binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyItem = listStory[position]

        val imgPhoto: ImageView = holder.binding.photo
        val tvName: TextView = holder.binding.username
        val tvDesc: TextView = holder.binding.description
        val tvDate: TextView = holder.binding.date

        Glide.with(holder.itemView.context)
            .load(storyItem.photoUrl)
            .into(holder.binding.photo)
        holder.binding.username.text = storyItem.name
        holder.binding.description.text = storyItem.description

        if (storyItem.createdAt != null) {
            holder.binding.date.text = DateConverter(storyItem.createdAt)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailStoryActivity::class.java)
            intent.putExtra("id", storyItem.id)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(imgPhoto, "photo"),
                    Pair(tvName, "name"),
                    Pair(tvDesc, "desc"),
                    Pair(tvDate, "date")
                )
            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())


        }
    }

    override fun getItemCount(): Int = listStory.size

}