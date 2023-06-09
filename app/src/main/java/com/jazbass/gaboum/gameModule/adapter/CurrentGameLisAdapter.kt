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

class CurrentGameLisAdapter(val isRow: Boolean) :
    ListAdapter<PlayerEntity, RecyclerView.ViewHolder>(CurrentGameDiffCallback()) {

    private lateinit var mContext: Context
    private val newScores = mutableMapOf<Int, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.entity_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val player = getItem(position)
        (holder as ViewHolder).setListener(player!!)
    }

    fun getNewScore(position: Int):Int{
        return newScores.getOrDefault(position,100)
    }

    public override fun getItem(i: Int): PlayerEntity? {
        return getItem(i)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = EntityItemBinding.bind(view)

        fun setListener(player: PlayerEntity) {
            with(binding) {
                if (!isRow) {
                    btnDecrease.visibility = View.INVISIBLE
                    btnIncrease.visibility = View.INVISIBLE
                    txtScorePlayer.text = player.score.toString().trimIndent()
                } else{

                    txtScorePlayer.text = newScores[adapterPosition].toString()

                    btnIncrease.setOnClickListener {
                        newScores[adapterPosition] = newScores.getOrDefault(position, 0) + 1
                        txtScorePlayer.text = newScores[adapterPosition].toString()
                    }

                    btnDecrease.setOnClickListener {
                        newScores[adapterPosition] = newScores.getOrDefault(position, 0) - 1
                        txtScorePlayer.text = newScores[adapterPosition].toString()
                    }
                }
                this.player.text = player.name
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