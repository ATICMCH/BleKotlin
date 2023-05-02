package com.jazbass.gaboum.gameModule

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.databinding.FragmentGameBinding
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel


class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var gameEntity: GameEntity
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        setFragmentResultListener("players"){ _, bundle ->
            binding.txtPlayer1.text = bundle.getString("player1")
            binding.txtPlayer2.text = bundle.getString("player2")
        }
        binding.txtScorePlayer1.text = "0"
        binding.txtScorePlayer2.text = "0"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
    }

    private fun setUpViewModel() {
        //gameViewModel.getGameSelected().observe(viewLifecycleOwner){
        //    gameEntity = it ?: GameEntity()
        //    if (it != null){
        //        isEditMode = true
        //        setUIGame(it)
        //    }else{
        //        isEditMode = false
        //    }
        //}
    }

    private fun setUIGame(gameEntity: GameEntity) {
        Log.i("Players", "${gameEntity.player1}, ${gameEntity.player2}" )
        with(binding){
            txtPlayer1.text = gameEntity.player1.trim()
            txtPlayer2.text = gameEntity.player2.trim()
            txtScorePlayer1.text = gameEntity.scorePlayer1.toString().trim()
            txtScorePlayer2.text = gameEntity.scorePlayer2.toString().trim()
        }
    }

}