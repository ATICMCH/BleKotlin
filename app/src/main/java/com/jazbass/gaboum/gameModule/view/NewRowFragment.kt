package com.jazbass.gaboum.gameModule.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.databinding.FragmentNewRowBinding
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel

class NewRowFragment : Fragment() {

    private lateinit var binding: FragmentNewRowBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var currentGameEntity: GameEntity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentNewRowBinding.inflate(inflater, container, false)
        //binding.btsSave
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
    }

    private fun saveRow(){
        currentGameEntity
    }

    private fun setUpViewModel(){
        gameViewModel.getGameSelected().observe(viewLifecycleOwner){
            currentGameEntity = it
            setUI(it)
        }
    }

    private fun setUI(gameEntity: GameEntity){

    }
}