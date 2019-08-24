package com.example.androidclient.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class LocalListFragment : Fragment() {
    private val mainViewModel by lazy {
        ViewModelProviders.of(context as MainActivity).get(MainViewModel::class.java)
    }

    private lateinit var recyclerView: RecyclerView
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
        val adapter = ContactsAdapter()
        recyclerView.adapter = adapter

        mainViewModel.localContacts.observe(context as MainActivity, Observer {
            adapter.setData(it)
        })

        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.readLocalContacts(context!!)
    }
}