package com.example.androidclient.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class LocalListFragment : Fragment() {
    private val mainViewModel by lazy {
        ViewModelProviders.of(context as MainActivity).get(MainViewModel::class.java)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactsAdapter

    companion object {
        fun newInstance(): LocalListFragment {
            return LocalListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container == null) {
            return View(context)
        }
        recyclerView = RecyclerView(context!!)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val layoutParam = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        recyclerView.layoutParams = layoutParam
        adapter = ContactsAdapter()
        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillLocal(recyclerView)
    }

    private fun fillLocal(recyclerView: RecyclerView) {
        val dataList = mainViewModel.readLocalContacts(context!!)
        adapter.setData(dataList)
        recyclerView.adapter = adapter
    }
}