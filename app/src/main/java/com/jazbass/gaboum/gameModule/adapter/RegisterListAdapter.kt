package com.jazbass.gaboum.gameModule.adapter

import android.view.View
import com.jazbass.gaboum.R
import android.view.ViewGroup
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jazbass.gaboum.common.entities.RoundEntity
import com.jazbass.gaboum.databinding.ItemHistBinding

class RegisterListAdapter(listener: OnClickListener): ListAdapter<RoundEntity, RecyclerView.ViewHolder>(GameDiffCallback()) {

    private lateinit var mContext: Context

    class GameDiffCallback : DiffUtil.ItemCallback<RoundEntity>() {

        override fun areItemsTheSame(oldItem: RoundEntity, newItem: RoundEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RoundEntity, newItem: RoundEntity): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_hist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemHistBinding.bind(view)
    }
}