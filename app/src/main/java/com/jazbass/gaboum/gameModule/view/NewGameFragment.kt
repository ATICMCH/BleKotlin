package com.jazbass.gaboum.gameModule.view

import android.util.Log
import android.view.View
import android.os.Bundle
import com.jazbass.gaboum.R
import android.view.ViewGroup
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import com.jazbass.gaboum.common.entities.GameEntity
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel
import com.google.android.material.textfield.TextInputLayout
import com.jazbass.gaboum.databinding.FragmentNewGameBinding

class NewGameFragment : Fragment() {

    //TODO guardar todos los players en BBDD

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
        addPlayers(5)
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

        gameViewModel.getResult().observe(viewLifecycleOwner) { result ->
            if (result is GameEntity) {
                if (result.id == 0L) launchGameFragment()
            }
        }
    }

    private fun setActionBar() {

    }

    private fun launchGameFragment(gameEntity: GameEntity = GameEntity()) {

        gameViewModel.setGameSelected(gameEntity)

        val fragment = GameFragment()
        val fragmentManager = parentFragmentManager

        Bundle().apply {
            putStringArrayList("playersList", arrayListOf("Claire", "Javier"))
        }.also {
            fragmentManager.setFragmentResult("players", it)
        }
        val transaction = fragmentManager.beginTransaction()

        with(transaction) {
            replace(R.id.containerMain, fragment)
            addToBackStack(null)
            commit()
        }
        binding.btnCreateGame.visibility = View.GONE
    }

    private fun addPlayers(amountPlayers: Int) {
        for (i: Int in 0..amountPlayers) {
            PlayerTextInputLayout(
                ContextThemeWrapper(
                    requireContext(),
                    R.style.CustomOutlinedBox
                ),
                playerNumber = i+1
            ).also {
                binding.parentLayout.addView(it)
            }
        }
    }

    class PlayerTextInputLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.style.CustomOutlinedBox,
        playerNumber: Int
    ) : TextInputLayout(context, attrs, defStyleAttr) {

        //private val editText: TextInputEditText

        init {
            val inputLayout = findViewById<TextInputLayout>(R.id.customTIL).apply {
                hint = "Player $playerNumber"
            }
            inflate(context, R.layout.custom_text_input_layout, this)
//            hint = context.getString(R.string.prompt_player2)
//            isCounterEnabled = true
//            counterMaxLength = context.resources.getInteger(R.integer.counter_max_name)
//            setStartIconDrawable(R.drawable.ic_player)
//
//            editText = TextInputEditText(context).apply {
//                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//                inputType = android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
//                counterMaxLength = context.resources.getInteger(R.integer.counter_max_name)
//            }
//            addView(editText)
        }
    }


}