package com.dicoding.picodiploma.loginwithanimation.utility

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.databinding.CardCellBinding
import com.dicoding.picodiploma.loginwithanimation.getTimeUpload
import com.dicoding.picodiploma.loginwithanimation.loadImage
import com.dicoding.picodiploma.loginwithanimation.local.LocalStories
import com.dicoding.picodiploma.loginwithanimation.view.detailstory.DetailStoryActivity

class CardViewHolder(
    private val cardCallBinding: CardCellBinding
) : RecyclerView.ViewHolder(cardCallBinding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(story: LocalStories){
        cardCallBinding.apply {
            tvUserStory.text = story.name
            ivCover.loadImage(story.photoUrl)
            tvCreatedDate.text = "Upload At ${getTimeUpload(story.createdAt)}"
            cardView.setOnClickListener{
                val moveDetail = Intent(itemView.context, DetailStoryActivity::class.java).also{
                    it.putExtra(DetailStoryActivity.DATA_STORY, story)
                    Log.d("OnClick Listener 2", story.toString())
                }
                cardView.context.startActivity(moveDetail)
            }
        }
    }
}