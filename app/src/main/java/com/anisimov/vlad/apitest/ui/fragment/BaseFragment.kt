package com.anisimov.vlad.apitest.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.anisimov.vlad.apitest.R

abstract class BaseFragment<VM : AndroidViewModel> : Fragment() {
    protected lateinit var viewModel: VM
    protected lateinit var nav: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(provideViewModelClass())
        nav = Navigation.findNavController(requireActivity(), R.id.navHostFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        return inflater.inflate(provideLayoutRes(), container, false)
    }

    protected abstract fun provideViewModelClass(): Class<VM>
    protected abstract fun provideLayoutRes(): Int
}