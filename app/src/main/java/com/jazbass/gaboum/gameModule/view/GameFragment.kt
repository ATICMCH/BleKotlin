package com.jazbass.gaboum.gameModule.view

import android.util.Log
import android.view.View
import android.os.Bundle
import java.util.ArrayList
import com.jazbass.gaboum.R
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.common.entities.PlayerEntity
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.jazbass.gaboum.databinding.FragmentGameBinding
import com.jazbass.gaboum.gameModule.model.GameInteractor
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel
import com.jazbass.gaboum.gameModule.adapter.CurrentGameLisAdapter

const val TAG = "GameFragment"

class GameFragment : Fragment() {

    private lateinit var gameViewModel: GameViewModel
    private lateinit var binding: FragmentGameBinding

    private lateinit var mAdapter: CurrentGameLisAdapter
    private lateinit var mLayout: RecyclerView.LayoutManager

    private val gameInteractor = GameInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false).apply {
            fab.setOnClickListener { launchNewRowFragment() }
        }
        setUpRecyclerView()
        return binding.root
    }


    private fun setUpViewModel() {
        gameViewModel.getPlayersList().observe(viewLifecycleOwner){
            setUpGame(it)
        }
    }


    private fun launchNewRowFragment() {
      val fragment = NewRowFragment()

        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()

        with(transaction) {
            replace(R.id.containerMain, fragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun setUpRecyclerView() {
        mAdapter = CurrentGameLisAdapter(isRow = false)
        mLayout = LinearLayoutManager(this.context)

        binding.recyclerView.apply {
            layoutManager = mLayout
            adapter = mAdapter
        }
    }


    private fun setUpGame(playerList: MutableList<PlayerEntity>) {
        playerList.map {
            Log.i("Name", it.name.toString())
        }
        mAdapter.submitList(playerList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
    }

    private fun setUIGame(gameEntity: GameEntity) {
        gameInteractor.getGamePlayers(gameEntity.id).also {
            mAdapter.submitList(it)
        }
    }
}

