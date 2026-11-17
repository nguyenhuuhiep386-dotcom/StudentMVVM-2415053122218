package com.example.studentmvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentmvvm.R
import com.example.studentmvvm.model.Student
import com.example.studentmvvm.repository.StudentRepository
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.ProtocolException

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    val allStudents: LiveData<List<Student>> = repository.allStudents.asLiveData(Dispatchers.IO)

    private val _working = MutableLiveData(false)
    val working: LiveData<Boolean> = _working

    private val _syncStatus = MutableLiveData<SyncStatus>()
    val syncStatus: LiveData<SyncStatus> = _syncStatus

    private val _message = MutableLiveData<Int?>()
    val message: LiveData<Int?> = _message

    data class SyncStatus(val textId: Int, val count: Int? = null)

    fun save(student: Student) = performWrite(R.string.student_saved) {
        if (student.id == 0) repository.insert(student) else repository.update(student)
    }

    fun delete(student: Student) = performWrite(R.string.student_deleted) {
        repository.delete(student)
    }

    fun clearMessage() {
        _message.value = null
    }

    fun refreshFromApi() {
        if (_working.value == true) return
        viewModelScope.launch {
            _working.value = true
            _syncStatus.value = SyncStatus(R.string.sync_running)
            try {
                val count = withContext(Dispatchers.IO) { repository.refreshFromApi() }
                _syncStatus.value = SyncStatus(R.string.sync_done, count)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: ProtocolException) {
                _syncStatus.value = SyncStatus(R.string.sync_invalid)
            } catch (exception: JsonParseException) {
                _syncStatus.value = SyncStatus(R.string.sync_invalid)
            } catch (exception: IOException) {
                _syncStatus.value = SyncStatus(R.string.sync_offline)
            } catch (exception: HttpException) {
                _syncStatus.value = SyncStatus(R.string.sync_error)
            } catch (exception: Exception) {
                _syncStatus.value = SyncStatus(R.string.sync_error)
            } finally {
                _working.value = false
            }
        }
    }

    private fun performWrite(successMessage: Int, action: suspend () -> Unit) {
        if (_working.value == true) return
        viewModelScope.launch {
            _working.value = true
            try {
                withContext(Dispatchers.IO) { action() }
                _message.value = successMessage
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _message.value = R.string.write_error
            } finally {
                _working.value = false
            }
        }
    }
}

class StudentViewModelFactory(private val repository: StudentRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == StudentViewModel::class.java)
        return StudentViewModel(repository) as T
    }
}
