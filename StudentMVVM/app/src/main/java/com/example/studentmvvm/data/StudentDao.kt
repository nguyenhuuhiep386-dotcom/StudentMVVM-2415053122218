package com.example.studentmvvm.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.studentmvvm.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT * FROM students_table ORDER BY id DESC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students_table WHERE student_code = :code ORDER BY id DESC LIMIT 1")
    suspend fun getStudentByCode(code: String): Student?

    @Query("SELECT * FROM students_table WHERE remote_id IS NOT NULL")
    suspend fun getImportedStudents(): List<Student>

    @Upsert
    suspend fun upsertStudents(students: List<Student>)

    @Delete
    suspend fun deleteStudents(students: List<Student>)
}
