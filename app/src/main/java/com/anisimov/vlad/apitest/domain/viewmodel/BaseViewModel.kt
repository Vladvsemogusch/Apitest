package com.anisimov.vlad.apitest.domain.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.anisimov.vlad.apitest.data.repository.Repository

abstract class BaseViewModel<R : Repository>(app: Application) : AndroidViewModel(app) {
    //  provideRepo doesn't depend on derived class state and never will so it's ok
    protected var repo: R = provideRepo()

    protected abstract fun provideRepo(): R
}