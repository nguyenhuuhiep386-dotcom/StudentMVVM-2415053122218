package com.example.studentmvvm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmvvm.R
import com.example.studentmvvm.databinding.FragmentStudentListBinding
import com.example.studentmvvm.model.Student
import com.google.android.material.snackbar.Snackbar

class StudentListFragment : Fragment() {
    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel get() = (requireActivity() as MainActivity).studentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = StudentAdapter(::showEditor, ::confirmDelete)
        adapter.stateRestorationPolicy =
            androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = adapter
        binding.fabAdd.setOnClickListener { showEditor(null) }
        binding.btnSync.setOnClickListener { viewModel.refreshFromApi() }

        viewModel.allStudents.observe(viewLifecycleOwner) { students ->
            adapter.submitList(students)
            binding.emptyLayout.isVisible = students.isEmpty()
            binding.tvCount.text = getString(R.string.student_count, students.size)
        }
        viewModel.working.observe(viewLifecycleOwner) { working ->
            binding.btnSync.isEnabled = !working
            binding.fabAdd.isEnabled = !working
        }
        viewModel.syncStatus.observe(viewLifecycleOwner) { status ->
            binding.tvSyncStatus.text = status.count?.let { getString(status.textId, it) }
                ?: getString(status.textId)
        }
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }

    private fun showEditor(student: Student?) {
        if (viewModel.working.value == true || childFragmentManager.findFragmentByTag("editor") != null) return
        AddStudentDialogFragment.newInstance(student).showNow(childFragmentManager, "editor")
    }

    private fun confirmDelete(student: Student) {
        if (viewModel.working.value == true || childFragmentManager.findFragmentByTag("delete") != null) return
        DeleteStudentDialogFragment.newInstance(student).showNow(childFragmentManager, "delete")
    }

    override fun onDestroyView() {
        binding.rvStudents.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
