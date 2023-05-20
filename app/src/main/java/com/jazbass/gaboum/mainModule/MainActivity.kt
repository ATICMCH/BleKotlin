package com.jazbass.gaboum.mainModule

import android.os.Bundle
import android.view.View
import com.jazbass.gaboum.R
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import com.jazbass.gaboum.common.entities.GameEntity
import androidx.recyclerview.widget.GridLayoutManager
import com.jazbass.gaboum.gameModule.view.GameFragment
import com.jazbass.gaboum.mainModule.model.MainInteractor
import com.jazbass.gaboum.gameModule.view.NewGameFragment
import com.jazbass.gaboum.databinding.ActivityMainBinding
import com.jazbass.gaboum.mainModule.viewModel.MainViewModel
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel
import com.jazbass.gaboum.mainModule.adapter.OnClickListener
import com.jazbass.gaboum.mainModule.adapter.GamesListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mAdapter: GamesListAdapter
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mGridLayout: GridLayoutManager

    private val mainInteractor = MainInteractor()
    private lateinit var mainViewModel: MainViewModel

    private lateinit var gameViewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnFab.setOnClickListener {
            mBinding.btnFab.visibility = View.GONE
            launchNewGameFragment()
        }

        setUpRecyclerView()
        setUpViewModel()
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

    private fun setUpViewModel(){
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainViewModel.getGames().observe(this){ games ->
            mAdapter.submitList(games)
        }

        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]

    }

    private fun launchNewGameFragment(){
        val fragment = NewGameFragment()
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        with(transaction){
            add(R.id.containerMain, fragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun onClick(gameEntity: GameEntity) {
        launchGameFragment(gameEntity)
    }

    private fun launchGameFragment(gameEntity: GameEntity? = GameEntity()){
        gameViewModel.setGameSelected(gameEntity!!.id)

        val fragment = GameFragment()
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        with(transaction){
            add(R.id.containerMain, fragment)
            addToBackStack(null)
            commit()
        }

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
        mainViewModel.deleteGame(gameEntity)
    }

}