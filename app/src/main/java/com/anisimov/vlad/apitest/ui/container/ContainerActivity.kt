package com.anisimov.vlad.apitest.ui.container

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.anisimov.vlad.apitest.R

class ContainerActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_container)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }
}