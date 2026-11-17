package com.example.studentmvvm.repository

import androidx.room.withTransaction
import com.example.studentmvvm.data.AppDatabase
import com.example.studentmvvm.model.Student
import com.example.studentmvvm.network.StudentApiService
import java.net.ProtocolException

class StudentRepository(
    private val database: AppDatabase,
    private val api: StudentApiService
) {
    private val dao = database.studentDao()
    val allStudents = dao.getAllStudents()

    suspend fun insert(student: Student) = dao.insertStudent(student)
    suspend fun update(student: Student) = dao.updateStudent(student)
    suspend fun delete(student: Student) = dao.deleteStudent(student)

    suspend fun refreshFromApi(): Int {
        val incoming = api.getStudents().map { dto ->
            val remoteId = dto?.remoteId?.trim().orEmpty()
            val name = dto?.fullName?.trim().orEmpty()
            val code = dto?.studentCode?.trim().orEmpty()
            val email = dto?.email?.trim().orEmpty()
            val gpa = dto?.gpa
            if (remoteId.isEmpty() || name.isEmpty() || code.isEmpty() ||
                gpa == null || gpa !in 0.0..4.0
            ) {
                throw ProtocolException("Invalid student data")
            }
            Student(fullName = name, studentCode = code, email = email, gpa = gpa, remoteId = remoteId)
        }
        val remoteIds = incoming.map { it.remoteId }.toSet()
        if (remoteIds.size != incoming.size) {
            throw ProtocolException("Duplicate remote student ID")
        }

        // Đồng bộ trong một giao dịch để Flow chỉ nhận danh sách đã cập nhật đầy đủ.
        database.withTransaction {
            val imported = dao.getImportedStudents()
            val existingByRemoteId = imported.associateBy { it.remoteId }
            dao.upsertStudents(incoming.map { student ->
                student.copy(id = existingByRemoteId[student.remoteId]?.id ?: 0)
            })
            dao.deleteStudents(imported.filter { it.remoteId !in remoteIds })
        }
        return incoming.size
    }
}
