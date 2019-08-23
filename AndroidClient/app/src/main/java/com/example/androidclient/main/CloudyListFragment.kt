package com.example.androidclient.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.androidclient.common.TOKEN
import com.example.androidclient.common.getSPString
import com.example.androidclient.common.pull
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.Exception

class CloudyListFragment : Fragment() {
    private val mainViewModel by lazy {
        ViewModelProviders.of(context as MainActivity).get(MainViewModel::class.java)
    }
    private lateinit var adapter: ContactsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private val scope = MainScope()

    companion object {
        fun newInstance(): CloudyListFragment {
            return CloudyListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        refreshLayout = SwipeRefreshLayout(context!!)
        val lp1 = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        refreshLayout.layoutParams = lp1

        recyclerView = RecyclerView(context!!)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val lp2 = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        recyclerView.layoutParams = lp2
        adapter = ContactsAdapter()
        recyclerView.adapter = adapter

        refreshLayout.addView(recyclerView)
        return refreshLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshLayout.setOnRefreshListener {
            refresh()
        }
        refresh()
    }

    private fun refresh() = scope.launch {
        refreshLayout.isRefreshing = true
        try {
            val token = mainViewModel.getToken()
            val header = mapOf("Authorization" to token)
            val pullResult = pull(header)
            val persons = pullResult.persons
            if (persons.isEmpty()) {
                Toast.makeText(context, "token无效或云端没有联系人，尝试重新登陆或上传数据", Toast.LENGTH_SHORT).show()
            }
            adapter.setData(persons)
        } catch (e: Exception) {
            Toast.makeText(context, "拉取云端联系人人失败，尝试重新登陆或设置url", Toast.LENGTH_SHORT).show()
        }
        refreshLayout.isRefreshing = false
    }
}