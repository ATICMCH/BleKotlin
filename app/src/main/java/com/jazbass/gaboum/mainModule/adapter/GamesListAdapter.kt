package com.jazbass.gaboum.mainModule.adapter

import android.view.View
import com.jazbass.gaboum.R
import android.view.ViewGroup
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jazbass.gaboum.databinding.ItemGameBinding
import com.jazbass.gaboum.common.entities.GaboumEntity

class GamesListAdapter(private var listener: OnClickListener) :
    ListAdapter<GaboumEntity, RecyclerView.ViewHolder>(GaboumDiffCallback()) {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_game, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class GaboumDiffCallback : DiffUtil.ItemCallback<GaboumEntity>() {

        override fun areItemsTheSame(oldItem: GaboumEntity, newItem: GaboumEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GaboumEntity, newItem: GaboumEntity): Boolean {
            return oldItem == newItem
        }

    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ItemGameBinding.bind(view)

    }

}