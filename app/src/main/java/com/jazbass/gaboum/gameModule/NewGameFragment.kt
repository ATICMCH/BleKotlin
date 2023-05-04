package com.jazbass.gaboum.gameModule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.jazbass.gaboum.GameApplication
import com.jazbass.gaboum.R
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.databinding.FragmentNewGameBinding
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel
import kotlinx.coroutines.delay

class NewGameFragment : Fragment() {

    private lateinit var binding: FragmentNewGameBinding
    private lateinit var gameViewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCreateGame.setOnClickListener { startGame() }
        setUpViewModel()
    }

    private fun setUpViewModel() {

    }

    private fun startGame() {
        GameEntity().apply {
            player1 = binding.etPlayer1.text.toString().trim()
            player2 = binding.etPlayer2.text.toString().trim()
            scorePlayer1 = 0
            scorePlayer2 = 0
        }.also {
            launchGameFragment(it)
        }
    }

    private fun launchGameFragment(gameEntity: GameEntity){
        gameViewModel.setGameSelected(gameEntity.id)

        val fragment = GameFragment()
        val fragmentManager = parentFragmentManager
        Bundle().apply {
            putString("player1", binding.etPlayer1.text.toString().trim())
            putString("player2", binding.etPlayer2.text.toString().trim())
        }.also {
            fragmentManager.setFragmentResult("players", it)
        }
        val transaction = fragmentManager.beginTransaction()

        with(transaction){
            replace(R.id.containerMain, fragment)
            addToBackStack(null)
            commit()
        }
    }


}