package com.anisimov.vlad.apitest.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory

abstract class BaseActivity<VM : AndroidViewModel> : AppCompatActivity(){
    private lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(provideViewModelClass())
    }

    protected abstract fun provideViewModelClass(): Class<VM>
}