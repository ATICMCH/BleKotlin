package com.jazbass.gaboum.gameModule.view

import android.view.View
import android.os.Bundle
import com.jazbass.gaboum.R
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.ContextThemeWrapper
import android.widget.EditText
import androidx.fragment.app.setFragmentResultListener
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
        for (i: Int in 1..amountPlayers) {

            TextInputLayout(
                ContextThemeWrapper(
                    requireContext(),
                    com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox
                )
            ).apply {
                LayoutInflater.from(context).inflate(R.layout.custom_text_input_layout, this, true)
                this.findViewById<EditText>(R.id.editText).hint = "Player $i"
            }.also {
                binding.parentLayout.addView(it)
            }
//            TextInputLayout(
//                requireContext()
//            ).apply {
//                LayoutInflater.from(context).inflate(R.layout.custom_text_input_layout, this, true)
//                hint = "Player $i"
//            }.also {
//                binding.parentLayout.addView(it)
//            }
        }
    }
}

//    class PlayerTextInputLayout @JvmOverloads constructor(
//        context: Context,
//        attrs: AttributeSet? = null,
//        defStyleAttr: Int = R.style.CustomOutlinedBox
//    ) : TextInputLayout(context, attrs, defStyleAttr) {
//
//        init {
//            //inflate(context, R.layout.custom_text_input_layout, this)
//
//            //            hint = context.getString(R.string.prompt_player2)
//            isCounterEnabled = true
//            counterMaxLength = context.resources.getInteger(R.integer.counter_max_name)
//            setStartIconDrawable(R.drawable.ic_player)
//
//            TextInputEditText(context).apply {
//                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//                inputType = android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
//                counterMaxLength = context.resources.getInteger(R.integer.counter_max_name)
//            }.also {
//                addView(it)
//            }
//        }
//    }
//}

