package com.jazbass.gaboum.gameModule.view

import android.os.Bundle
import android.view.View
import com.jazbass.gaboum.R
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.databinding.FragmentNewGameBinding
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel

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
        binding.btnCreateGame.setOnClickListener { launchGameFragment() }
        setUpViewModel()
    }

    private fun setUpViewModel() {

    }


    private fun launchGameFragment(gameEntity: GameEntity = GameEntity()){
        gameViewModel.setGameSelected(gameEntity.id)

        val fragment = GameFragment()
        val fragmentManager = parentFragmentManager

        val transaction = fragmentManager.beginTransaction()

        with(transaction){
            replace(R.id.containerMain, fragment)
            addToBackStack(null)
            commit()
        }
    }
}