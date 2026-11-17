package com.example.studentmvvm.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studentmvvm.R
import com.example.studentmvvm.databinding.ItemStudentRoomBinding
import com.example.studentmvvm.model.Student
import java.util.Locale

class StudentAdapter(
    private val onEditClick: (Student) -> Unit,
    private val onDeleteClick: (Student) -> Unit
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StudentViewHolder(private val binding: ItemStudentRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            val context = binding.root.context
            val lastName = student.fullName.substringAfterLast(' ')
            binding.tvAvatar.text = lastName.take(1).uppercase(Locale.ROOT)
            binding.tvName.text = student.fullName
            binding.tvCode.text = context.getString(R.string.student_code_format, student.studentCode)
            binding.tvEmail.text = student.email.ifBlank { context.getString(R.string.no_email) }
            binding.tvGpa.text = context.getString(R.string.gpa_format, student.gpa)
            binding.tvSource.setText(if (student.remoteId == null) R.string.source_local else R.string.source_api)
            binding.btnEdit.setOnClickListener { onEditClick(student) }
            binding.btnDelete.setOnClickListener { onDeleteClick(student) }
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Student, newItem: Student) = oldItem == newItem
    }
}
