package com.example.androidclient.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.androidclient.event.TokenEvent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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

        mainViewModel.token.observe(context as MainActivity, Observer {
            refresh()
        })

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
            val data = mainViewModel.pull()
            if (data.isEmpty()) {
                Toast.makeText(context, "token无效或云端没有联系人，尝试重新登陆或上传数据", Toast.LENGTH_SHORT).show()
                return@launch
            }
            adapter.setData(data)
        } catch (e: Exception) {
            Toast.makeText(context, "拉取云端联系人人失败，尝试重新登陆或设置url", Toast.LENGTH_SHORT).show()
        }
        refreshLayout.isRefreshing = false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTokenEvent(tokenEvent: TokenEvent) {
        mainViewModel.token.value = tokenEvent.token
    }
}