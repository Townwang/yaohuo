package com.townwang.yaohuo.common
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

typealias T = Any
open class UIViewModel : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private val _loading = MutableLiveData<Boolean>()
    private val _error = MutableLiveData<Throwable>()
    private val _sqlite = MutableLiveData<T>()
    val loading = _loading.asLiveData()
    val error = _error.asLiveData()
    fun launchTask(task: suspend CoroutineScope.() -> Unit) = uiScope.launch {
        // show ui loading
        _loading.value = true
        // run task
        runCatching {
            task()
        }.onFailure {
            _error.value = it
        }
        // hide ui loading if necessary
        if (job.children.count() == 0) {
            _loading.value = false
        } else {
            val otherJobs = job.children.filter { it != this }
            if (otherJobs.none { it.isActive }) _loading.value = false
        }
    }
    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}