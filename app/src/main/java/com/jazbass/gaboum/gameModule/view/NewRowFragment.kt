package com.jazbass.gaboum.gameModule.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.jazbass.gaboum.databinding.FragmentNewRowBinding
import com.jazbass.gaboum.gameModule.adapter.CurrentGameLisAdapter
import com.jazbass.gaboum.gameModule.viewModel.GameViewModel

class NewRowFragment : Fragment() {

    private lateinit var gameViewModel: GameViewModel
    private lateinit var binding: FragmentNewRowBinding

    private lateinit var mAdapter: CurrentGameLisAdapter
    private lateinit var mLayout: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentNewRowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setUpViewModel()
    }

    private fun setUpRecyclerView() {
        mAdapter = CurrentGameLisAdapter(isRow = true)
        mLayout = LinearLayoutManager(this.context)

        binding.recyclerView.apply {
            layoutManager = mLayout
            adapter = mAdapter
        }
    }

    private fun setUpViewModel(){
        gameViewModel.getPlayersList().observe(viewLifecycleOwner){
            mAdapter.submitList(it)
        }
    }
}