package com.townwang.yaohuo.ui.fragment.send

import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.UIViewModel
import com.townwang.yaohuo.common.asLiveData
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.YaoCdnReq
import java.io.File

class UploadFileModel(private val repo: Repo) : UIViewModel() {
    private val _fileStatus = MutableLiveData<Boolean>()
    val fileStatus = _fileStatus.asLiveData()
    private val _list = MutableLiveData<YaoCdnReq>()
    val list = _list.asLiveData()
    fun uploadFile(file: File) = launchTask {
        val rep = repo.uploadFile(file)
            rep.url ?: return@launchTask
            _list.value = rep
            _fileStatus.value = true
    }
    fun remove(delete: String?) = launchTask {
        delete ?: return@launchTask
        repo.deleteImg(delete)
    }
}