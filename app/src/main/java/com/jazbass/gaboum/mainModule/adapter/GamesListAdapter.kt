package com.jazbass.gaboum.mainModule.adapter

import android.view.View
import com.jazbass.gaboum.R
import android.view.ViewGroup
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.databinding.ItemGameBinding

class GamesListAdapter(private var listener: OnClickListener) :
    ListAdapter<GameEntity, RecyclerView.ViewHolder>(GameDiffCallback()) {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_game, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val game = getItem(position)

        with(holder as ViewHolder) {
            setListener(game)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemGameBinding.bind(view)

        fun setListener(gameEntity: GameEntity) {
            with(binding.root) {
                setOnClickListener { listener.onClick(gameEntity) }
                setOnLongClickListener {
                    listener.onDeleteGame(gameEntity)
                    true
                }
            }
        }
    }


    class GameDiffCallback : DiffUtil.ItemCallback<GameEntity>() {

        override fun areItemsTheSame(oldItem: GameEntity, newItem: GameEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GameEntity, newItem: GameEntity): Boolean {
            return oldItem == newItem
        }

    }

}