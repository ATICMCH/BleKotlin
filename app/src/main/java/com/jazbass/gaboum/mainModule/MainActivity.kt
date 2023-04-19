package com.jazbass.gaboum.mainModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.jazbass.gaboum.R
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.databinding.ActivityMainBinding
import com.jazbass.gaboum.mainModule.adapter.GamesListAdapter
import com.jazbass.gaboum.mainModule.adapter.OnClickListener
import com.jazbass.gaboum.mainModule.model.MainInteractor

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: GamesListAdapter
    private lateinit var mGridLayout: GridLayoutManager

    private val mainInteractor = MainInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        mAdapter = GamesListAdapter(this)
        mGridLayout = GridLayoutManager(this, 1)

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun launchGameFragment(gameEntity: GameEntity? = GameEntity()){
        if (gameEntity?.id == 0L ){
            //New game
        }else{
            //Edit game
        }
    }

    override fun onClick(gameEntity: GameEntity) {
        launchGameFragment(gameEntity)
    }

    override fun onDeleteGame(gameEntity: GameEntity) {

    }

    private fun confirmDelete(gameEntity: GameEntity){

    }


}