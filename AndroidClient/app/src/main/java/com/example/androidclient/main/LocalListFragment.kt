package com.example.androidclient.main

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidclient.entity.Person


class LocalListFragment : Fragment() {
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
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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
        val dataList = readLocalContacts()
        adapter.setData(dataList)
        recyclerView.adapter = adapter
    }

    private fun readLocalContacts(): List<Person> {
        val cursor = context?.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (cursor != null) {
            val result = ArrayList<Person>()
            while (cursor.moveToNext()) {
                // 获取联系人姓名
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                // 获取联系人手机号
                val number =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                result.add(Person(name, number))
            }
            cursor.close()
            return result
        }

        return emptyList()
    }
}