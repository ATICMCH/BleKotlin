package com.jazbass.gaboum.gameModule

import android.util.Log
import android.view.View
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jazbass.gaboum.common.entities.GameEntity
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jazbass.gaboum.databinding.FragmentGameBinding
import com.jazbass.gaboum.gameModule.adapter.CurrentGameLisAdapter
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel

class GameFragment : Fragment() {

    private var isNewGame = false
    private lateinit var gameViewModel: GameViewModel
    private lateinit var binding: FragmentGameBinding

    private lateinit var mAdapter: CurrentGameLisAdapter
    private lateinit var mGridLayout: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false).apply {
            fab.setOnClickListener { saveRow() }
        }
        setUpRecyclerView()
        return binding.root
    }

    private fun setUpRecyclerView() {
        mAdapter = CurrentGameLisAdapter()
        mGridLayout = GridLayoutManager(this.context, 1)

        binding.recyclerView.apply {
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun saveRow() {

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
                Log.i("notnull", "1")
            } else {
                isNewGame = true
            }
        }
    }

    private fun setUIGame(gameEntity: GameEntity) {
        with(binding) {

        }
    }
}