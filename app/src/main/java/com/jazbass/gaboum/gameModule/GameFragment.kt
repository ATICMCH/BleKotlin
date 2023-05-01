package com.jazbass.gaboum.gameModule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.databinding.FragmentGameBinding
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel


class GameFragment : Fragment() {

    private lateinit var mBinding: FragmentGameBinding
    private lateinit var mGameViewModel: GameViewModel
    private lateinit var mGameEntity: GameEntity
    private var mIsEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentGameBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()
    }

    private fun setUpViewModel() {
        mGameViewModel.getGameSelected().observe(viewLifecycleOwner){
            mGameEntity = it ?: GameEntity()
            if (it != null){
                mIsEditMode = true
                setUIGame(it)
            }else{
                mIsEditMode = false
            }
        }
    }

    private fun setUIGame(gameEntity: GameEntity) {
        with(mBinding){
            txtPlayer1.text = gameEntity.player1
            txtPlayer2.text = gameEntity.player2
            txtScorePlayer1.text = gameEntity.scorePlayer1.toString()
            txtScorePlayer2.text = gameEntity.scorePlayer2.toString()
        }
    }

}