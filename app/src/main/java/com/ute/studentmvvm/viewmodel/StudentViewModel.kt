package com.ute.studentmvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    val allStudents: LiveData<List<Student>> =
        repository.allStudents.asLiveData(Dispatchers.IO)

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage


    fun insert(student: Student) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existing = repository.getStudentByCode(
                    student.studentCode
                )

                if (existing != null) {
                    _statusMessage.postValue(
                        "Mã sinh viên ${student.studentCode} đã tồn tại!"
                    )
                    return@launch
                }

                repository.insert(student)

                _statusMessage.postValue(
                    "Đã thêm sinh viên: ${student.fullName}"
                )
            } catch (e: Exception) {
                _statusMessage.postValue(
                    "Không thể thêm sinh viên. Kiểm tra mã sinh viên!"
                )
            }
        }

    fun delete(student: Student) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.delete(student)
                _statusMessage.postValue(
                    "Đã xóa sinh viên: ${student.fullName}"
                )
            } catch (e: Exception) {
                _statusMessage.postValue(
                    "Lỗi khi xóa: ${e.message}"
                )
            }
        }

    fun update(student: Student) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existing = repository.getStudentByCode(
                    student.studentCode
                )

                if (existing != null && existing.id != student.id) {
                    _statusMessage.postValue(
                        "Mã sinh viên ${student.studentCode} đã tồn tại!"
                    )
                    return@launch
                }

                repository.update(student)

                _statusMessage.postValue(
                    "Đã cập nhật sinh viên: ${student.fullName}"
                )
            } catch (e: Exception) {
                _statusMessage.postValue(
                    "Không thể cập nhật sinh viên!"
                )
            }
        }

    fun syncFromCloud() =
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.syncFromCloud()

            if (result.isSuccess) {
                _statusMessage.postValue(
                    "Đã đồng bộ ${result.getOrNull()} sinh viên!"
                )
            } else {
                _statusMessage.postValue(
                    "Chưa thể kết nối Cloud. Sử dụng dữ liệu cục bộ."
                )
            }
        }
}
