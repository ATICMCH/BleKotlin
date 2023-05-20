package com.jazbass.gaboum.gameModule.view

import android.util.Log
import android.view.View
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.jazbass.gaboum.common.entities.GameEntity
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.jazbass.gaboum.R
import com.jazbass.gaboum.common.entities.PlayerEntity
import com.jazbass.gaboum.databinding.FragmentGameBinding
import com.jazbass.gaboum.gameModule.model.GameInteractor
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel
import com.jazbass.gaboum.gameModule.adapter.CurrentGameLisAdapter
import java.util.ArrayList

const val TAG = "GameFragment"

class GameFragment : Fragment() {

    private var isNewGame = false
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
        setFragmentResultListener("players") { _, bundle ->
            val players = bundle.getStringArrayList("playersList")
            setUpRecyclerView()
            setUpGame(players)
        }
        return binding.root
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
        mAdapter = CurrentGameLisAdapter()
        mLayout = LinearLayoutManager(this.context)

        binding.recyclerView.apply {
            layoutManager = mLayout
            adapter = mAdapter
        }
    }


    private fun setUpGame(players: ArrayList<String>?) {
        val playerList: MutableList<PlayerEntity> = mutableListOf()
        for(player in players!!){
            playerList.add(PlayerEntity(gameId = 0L, score = 0, name = player))
            Log.i(TAG, player)
        }
        mAdapter.submitList(playerList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
    }

    private fun setUpViewModel() {
        gameViewModel.getGameSelected().observe(viewLifecycleOwner) {
            if (it != null) {
                isNewGame = false
                setUIGame(it)
            } else {
                isNewGame = true
            }
        }
    }

    private fun setUIGame(gameEntity: GameEntity) {

    }
}

