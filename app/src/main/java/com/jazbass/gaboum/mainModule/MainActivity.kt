package com.jazbass.gaboum.mainModule

import android.os.Bundle
import com.jazbass.gaboum.R
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jazbass.gaboum.common.entities.GameEntity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jazbass.gaboum.mainModule.model.MainInteractor
import com.jazbass.gaboum.databinding.ActivityMainBinding
import com.jazbass.gaboum.mainModule.adapter.OnClickListener
import com.jazbass.gaboum.mainModule.adapter.GamesListAdapter

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
        val items = resources.getStringArray(R.array.array_options_items)

        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar partida?")
            .setItems(items){_, i ->
                when(i){
                    0 -> confirmDelete(gameEntity)
                    1 -> onBackPressed()
                }
            }.show()

    }

    private fun confirmDelete(gameEntity: GameEntity){

    }


}