package com.jazbass.gaboum.mainModule

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.jazbass.gaboum.R
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.jazbass.gaboum.common.entities.GameEntity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.button.MaterialButton
import com.jazbass.gaboum.gameModule.view.GameFragment
import com.jazbass.gaboum.mainModule.model.MainInteractor
import com.jazbass.gaboum.gameModule.view.NewGameFragment
import com.jazbass.gaboum.databinding.ActivityMainBinding
import com.jazbass.gaboum.mainModule.viewModel.MainViewModel
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel
import com.jazbass.gaboum.mainModule.adapter.OnClickListener
import com.jazbass.gaboum.mainModule.adapter.GamesListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mAdapter: GamesListAdapter
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mGridLayout: GridLayoutManager

    private val mainInteractor = MainInteractor()
    private lateinit var mainViewModel: MainViewModel

    private lateinit var gameViewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnFab.setOnClickListener {
            mBinding.btnFab.visibility = View.GONE
            startGame()
        }

        setUpRecyclerView()
        setUpViewModel()
    }

    private fun setUpRecyclerView() {
        mAdapter = GamesListAdapter(this)
        mGridLayout = GridLayoutManager(this, 1)

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun setUpViewModel() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainViewModel.getGames().observe(this) { games ->
            mAdapter.submitList(games)
        }
        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]

    }

    private fun startGame() {
        val editText = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText("1")
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val currentValue = s.toString().toIntOrNull() ?: 0
                    if (currentValue < 1) {
                        setText("1")
                    } else if (currentValue > 99) {
                        setText("99")
                    }
                }
                override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int,after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        val incrementButton = Button(this, null, android.R.attr.borderlessButtonStyle).apply {
            text = "+"
            backgroundTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.white)
            setOnClickListener {
                val currentValue = editText.text.toString().toIntOrNull() ?: 1
                editText.setText((currentValue + 1).toString())
            }
        }

        val decrementButton = Button(this, null, android.R.attr.borderlessButtonStyle).apply {
            text = "-"
            backgroundTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.white)
            setOnClickListener {
                val currentValue = editText.text.toString().toIntOrNull() ?: 1
                if (currentValue > 1) {
                    editText.setText((currentValue - 1).toString())
                }
            }
        }

        val layout = LinearLayout(ContextThemeWrapper(this, R.style.Theme_Gaboum)).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
            addView(decrementButton)
            addView(editText)
            addView(incrementButton)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_new_game_title)
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                val number = editText.text.toString().toInt()
                launchNewGameFragment(number)
            }
            .setNegativeButton("CANCEL") { _, _ -> }
            .show()
    }

    private fun launchNewGameFragment(amountPlayers: Int) {


        val fragment = NewGameFragment()
        val fragmentManager = supportFragmentManager

        Bundle().apply {
            putInt("playersAmount", amountPlayers)
        }.also {
            fragmentManager.setFragmentResult("players", it)
        }

        val transaction = fragmentManager.beginTransaction()

        with(transaction) {
            add(R.id.containerMain, fragment)
            addToBackStack(null)
            commit()
        }
    }

    override fun onClick(gameEntity: GameEntity) {
        launchGameFragment(gameEntity)
    }

    private fun launchGameFragment(gameEntity: GameEntity? = GameEntity()) {
        gameViewModel.setGameSelected(gameEntity!!)

        val fragment = GameFragment()
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        with(transaction) {
            add(R.id.containerMain, fragment)
            addToBackStack(null)
            commit()
        }

    }

    override fun onDeleteGame(gameEntity: GameEntity) {
        val items = resources.getStringArray(R.array.array_options_items)

        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar partida?")
            .setItems(items) { _, i ->
                when (i) {
                    0 -> confirmDelete(gameEntity)
                    1 -> onBackPressed()
                }
            }.show()

    }

    private fun confirmDelete(gameEntity: GameEntity) {
        mainViewModel.deleteGame(gameEntity)
    }

}