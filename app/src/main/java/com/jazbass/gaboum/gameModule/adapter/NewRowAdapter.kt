package com.jazbass.gaboum.gameModule.adapter

import android.view.View
import com.jazbass.gaboum.R
import android.view.ViewGroup
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jazbass.gaboum.common.entities.PlayerEntity
import com.jazbass.gaboum.databinding.EntityItemBinding

class NewRowAdapter() :
    ListAdapter<PlayerEntity, RecyclerView.ViewHolder>(CurrentGameDiffCallback()) {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.entity_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val player = getItem(position)

        with(holder as ViewHolder) {
            setListener(player)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = EntityItemBinding.bind(view)

        fun setListener(player: PlayerEntity) {
            with(binding) {
                scorePlayer.text = player.score.toString().trim()
                this.player.text = player.name

                btnIncrease.setOnClickListener {
                    var score = scorePlayer.text.toString().toInt()
                    score ++
                    binding.scorePlayer.text = score.toString()
                }
                btnDecrease.setOnClickListener {
                    var score = scorePlayer.text.toString().toInt()
                    score --
                    binding.scorePlayer.text = score.toString()
                }
            }
        }
    }

    class CurrentGameDiffCallback : DiffUtil.ItemCallback<PlayerEntity>() {

        override fun areItemsTheSame(oldItem: PlayerEntity, newItem: PlayerEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlayerEntity, newItem: PlayerEntity): Boolean {
            return oldItem == newItem
        }

    }

}