package com.example.androidclient


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val result = checkPermissions()
        if(result){
            findNavController().navigate(R.id.action_splashFragment_to_addressListFragment)
        }
    }

    private fun checkPermissions(): Boolean {
        val permissions =
            listOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG)
        val requestList = ArrayList<String>()
        for (permission in permissions) {
            val check = ContextCompat.checkSelfPermission(requireActivity(), permission)
            if (check == PackageManager.PERMISSION_DENIED) {
                requestList.add(permission)
            }
        }
        if (requestList.isEmpty()) {
            return true
        }
        requestPermissions(permissions.toTypedArray(), 101)
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) {
            return
        }
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }
        findNavController().navigate(R.id.action_splashFragment_to_addressListFragment)
    }
}
