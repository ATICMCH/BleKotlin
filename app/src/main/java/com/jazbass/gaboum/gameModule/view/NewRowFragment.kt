package com.jazbass.gaboum.gameModule.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.jazbass.gaboum.common.entities.PlayerEntity
import com.jazbass.gaboum.databinding.FragmentNewRowBinding
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel
import com.jazbass.gaboum.gameModule.adapter.CurrentGameLisAdapter

class NewRowFragment : Fragment() {

    private lateinit var mGameViewModel: GameViewModel
    private lateinit var mBinding: FragmentNewRowBinding
    private lateinit var mAdapter: CurrentGameLisAdapter
    private lateinit var mLayout: RecyclerView.LayoutManager

    private lateinit var mPlayersList: List<PlayerEntity>
    private lateinit var mNewScoresList: Map<PlayerEntity, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNewRowBinding.inflate(inflater, container, false)
        mBinding.fab.setOnClickListener { saveRow() }
        return mBinding.root
    }

    private fun saveRow() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setUpViewModel()
    }

    private fun setUpRecyclerView() {
        mAdapter = CurrentGameLisAdapter(isRow = true)
        mLayout = LinearLayoutManager(this.context)

        mBinding.recyclerView.apply {
            layoutManager = mLayout
            adapter = mAdapter
        }
    }

    private fun setUpViewModel(){
        mGameViewModel.getPlayersList().observe(viewLifecycleOwner){
            mPlayersList = it
            mAdapter.submitList(mPlayersList)
        }
    }
}