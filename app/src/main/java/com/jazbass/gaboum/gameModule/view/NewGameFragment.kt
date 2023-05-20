package com.jazbass.gaboum.gameModule.view

import android.os.Bundle
import android.view.View
import com.jazbass.gaboum.R
import android.view.ViewGroup
import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jazbass.gaboum.common.entities.GameEntity
import com.google.android.material.textfield.TextInputLayout
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
        with(binding){
            btnCreateGame.setOnClickListener { launchGameFragment() }
            btnNewPlayer.setOnClickListener { addNewPlayer() }
        }


        setUpViewModel()
    }

    private fun setUpViewModel() {

    }

    private fun launchGameFragment(gameEntity: GameEntity = GameEntity()) {
        gameViewModel.setGameSelected(gameEntity.id)

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

    private fun addNewPlayer() {
        val customTextInputLayout = PlayerTextInputLayout(ContextThemeWrapper(requireContext(),R.style.CustomOutlinedBox))
        binding.parentLayout.addView(customTextInputLayout)
    }

    class PlayerTextInputLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.style.CustomOutlinedBox
    ) :  TextInputLayout(context, attrs, defStyleAttr) {

        //private val editText: TextInputEditText

        init {
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