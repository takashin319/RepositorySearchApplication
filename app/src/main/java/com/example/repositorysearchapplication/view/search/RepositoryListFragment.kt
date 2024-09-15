package com.example.repositorysearchapplication.view.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repositorysearchapplication.R
import com.example.repositorysearchapplication.databinding.FragmentRepositoryListBinding
import com.example.repositorysearchapplication.model.adapter.RepositoryListAdapter
import com.example.repositorysearchapplication.model.database.RepositoryEntity
import com.example.repositorysearchapplication.viewmodel.SearchViewModel

class RepositoryListFragment : Fragment() {
    private var _binding: FragmentRepositoryListBinding? = null
    private val binding get() = _binding!!
    private val _searchViewModel: SearchViewModel by activityViewModels()
    private val adapter = RepositoryListAdapter()

    // LiveDataのオブザーバクラス
    private inner class RepositoryListObserver : Observer<List<RepositoryEntity>> {
        override fun onChanged(value: List<RepositoryEntity>) {
            adapter.submitList(value)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRepositoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // LiveDataにオブザーバを登録
        _searchViewModel.repositoryList.observe(
            viewLifecycleOwner,
            RepositoryListObserver(),
        )

        // RecyclerViewの設定
        binding.rvRepositoryList.adapter = adapter
        binding.rvRepositoryList.layoutManager =
            LinearLayoutManager(this.context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        binding.rvRepositoryList.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL,
            ),
        )

        // リストをクリックしたときの処理
        adapter.setOnItemClickListener(
            object : RepositoryListAdapter.OnRepositoryItemClickListener {
                override fun onItemClick(data: RepositoryEntity) {
                    _searchViewModel.selectRepository = data
                    findNavController().navigate(R.id.action_repositoryListFragment_to_repositoryDetailFragment)
                }
            },
        )

        // 検索ボタンをクリックしたときの処理
        binding.btnSearch.setOnClickListener {
            hideSoftKeyboard()
            if (binding.txtSearchWord.text.isNullOrBlank()) {
                Toast.makeText(context, "キーワードを入力してください", Toast.LENGTH_SHORT).show()
            } else {
                _searchViewModel.searchWord = binding.txtSearchWord.text.toString()
                _searchViewModel.clearRepositoryList()
                _searchViewModel.addRepositoryList()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // キーボードを非表示にするメソッド
    private fun hideSoftKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}