package com.example.studentmvvm.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.studentmvvm.R
import com.example.studentmvvm.databinding.DialogAddStudentBinding
import com.example.studentmvvm.model.Student
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddStudentDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddStudentBinding.inflate(LayoutInflater.from(requireContext()))
        val student = arguments?.readStudent()
        if (student != null) {
            binding.etName.setText(student.fullName)
            binding.etCode.setText(student.studentCode)
            binding.etEmail.setText(student.email)
            binding.etGpa.setText(student.gpa.toString())
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (student == null) R.string.add_student else R.string.edit_student)
            .setView(binding.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = binding.etName.text.toString().trim()
                val code = binding.etCode.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val gpa = binding.etGpa.text.toString().trim().replace(',', '.').toDoubleOrNull()
                val nameValid = name.isNotEmpty()
                val codeValid = code.isNotEmpty()
                val emailValid = email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
                val gpaValid = gpa != null && gpa in 0.0..4.0

                binding.nameField.error = if (nameValid) null else getString(R.string.name_required)
                binding.codeField.error = if (codeValid) null else getString(R.string.code_required)
                binding.emailField.error = if (emailValid) null else getString(R.string.email_invalid)
                binding.gpaField.error = if (gpaValid) null else getString(R.string.gpa_invalid)

                val firstInvalidField = when {
                    !nameValid -> binding.etName
                    !codeValid -> binding.etCode
                    !emailValid -> binding.etEmail
                    !gpaValid -> binding.etGpa
                    else -> null
                }
                if (firstInvalidField != null) {
                    firstInvalidField.requestFocus()
                    return@setOnClickListener
                }

                val formStudent = Student(
                    id = student?.id ?: 0,
                    fullName = name,
                    studentCode = code,
                    email = email,
                    gpa = checkNotNull(gpa),
                    remoteId = student?.remoteId
                )
                (requireActivity() as MainActivity).studentViewModel.save(formStudent)
                dismiss()
            }
        }
        return dialog
    }

    companion object {
        fun newInstance(student: Student?) = AddStudentDialogFragment().apply {
            arguments = student?.toArguments()
        }
    }
}

class DeleteStudentDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val student = requireArguments().readStudent()
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_student)
            .setMessage(getString(R.string.delete_confirmation, student.fullName))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                (requireActivity() as MainActivity).studentViewModel.delete(student)
            }
            .create()
    }

    companion object {
        fun newInstance(student: Student) = DeleteStudentDialogFragment().apply {
            arguments = student.toArguments()
        }
    }
}

private fun Student.toArguments() = bundleOf(
    "id" to id,
    "name" to fullName,
    "code" to studentCode,
    "email" to email,
    "gpa" to gpa,
    "remote_id" to remoteId
)

private fun Bundle.readStudent() = Student(
    id = getInt("id"),
    fullName = checkNotNull(getString("name")),
    studentCode = checkNotNull(getString("code")),
    email = checkNotNull(getString("email")),
    gpa = getDouble("gpa"),
    remoteId = getString("remote_id")
)
