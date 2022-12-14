package com.dicoding.picodiploma.loginwithanimation.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import com.dicoding.picodiploma.loginwithanimation.R

class LoadingDialog(private val mActivity: Activity) {
    private lateinit var isDialog: AlertDialog
    @SuppressLint("InflateParams")
    fun startLoading(){
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_item, null)
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }
    fun dismissLoading(){
        isDialog.dismiss()
    }
}