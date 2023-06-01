package com.jazbass.gaboum.gameModule.view

import android.os.Bundle
import android.view.View
import com.jazbass.gaboum.R
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import com.jazbass.gaboum.common.entities.GameEntity
import androidx.fragment.app.setFragmentResultListener
import com.jazbass.gaboum.common.entities.PlayerEntity
import com.jazbass.gaboum.databinding.FragmentNewGameBinding
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText

class NewGameFragment : Fragment() {

    //TODO guardar todos los players en BBDD

    private lateinit var binding: FragmentNewGameBinding
    private lateinit var gameViewModel: GameViewModel

    private val tilList = mutableListOf<TextInputLayout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewGameBinding.inflate(inflater, container, false)
        setFragmentResultListener("players"){_, bundle ->
            bundle.getInt("playersAmount").also {
                addPlayers(it)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModel()

        with(binding) {
            btnCreateGame.setOnClickListener { launchGameFragment() }
        }
    }

    private fun setUpViewModel() {

        gameViewModel.isShowProgress().observe(viewLifecycleOwner) { isShowProgress ->
            binding.progressBar.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }

//        gameViewModel.getResult().observe(viewLifecycleOwner) { result ->
//            if (result is GameEntity) {
//                if (result.id == 0L) launchGameFragment()
//            }
//        }
    }

    private fun setActionBar() {

    }

    private fun launchGameFragment(gameEntity: GameEntity = GameEntity()) {

        val playerList = mutableListOf<PlayerEntity>()

        tilList.map { til ->
            val name = if (til.editText?.text.toString() == "") til.hint as String
                    else til.editText?.text.toString()
            playerList.add(PlayerEntity(name = name))
        }

        gameViewModel.setPlayersList(playerList)
        //gameViewModel.setGameSelected(gameEntity)

        val fragment = GameFragment()
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().run {
            replace(R.id.containerMain, fragment)
            addToBackStack(null)
            commit()
        }
        binding.btnCreateGame.visibility = View.GONE
    }

    private fun addPlayers(amountPlayers: Int) {
        for (i: Int in 1..amountPlayers) {
            TextInputLayout(
                ContextThemeWrapper(
                    requireContext(),
                    com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox
                )
            ).apply {
                val textInputEditText = TextInputEditText(requireContext())
                textInputEditText.hint = "Player $i"
                this.addView(textInputEditText)
            }.also {
                binding.parentLayout.addView(it)
                tilList.add(it)
            }
        }
    }
}


