package com.ute.studentmvvm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ute.studentmvvm.R
import com.ute.studentmvvm.data.AppDatabase
import com.ute.studentmvvm.databinding.FragmentStudentListBinding
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.repository.StudentRepository
import com.ute.studentmvvm.ui.adapter.StudentAdapter
import com.ute.studentmvvm.viewmodel.StudentViewModel
import com.ute.studentmvvm.viewmodel.StudentViewModelFactory

class StudentListFragment : Fragment() {

    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudentViewModel by activityViewModels {
        val database = AppDatabase.getDatabase(requireContext())
        StudentViewModelFactory(
            StudentRepository(database.studentDao())
        )
    }

    private lateinit var studentAdapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        studentAdapter = StudentAdapter(
            onStudentClick = { student ->
                AddStudentDialogFragment.newInstance(student)
                    .show(parentFragmentManager, "EditStudent")
            },
            onDeleteClick = { student ->
                confirmDelete(student)
            }
        )

        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
        }

        binding.fabAdd.setOnClickListener {
            AddStudentDialogFragment.newInstance()
                .show(parentFragmentManager, "AddStudent")
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_sync) {
                viewModel.syncFromCloud()
                true
            } else {
                false
            }
        }

        viewModel.allStudents.observe(viewLifecycleOwner) { students ->
            studentAdapter.submitList(students)

            binding.tvStudentCountBadge.text =
                "${students.size} sinh viên"

            binding.layoutEmpty.visibility =
                if (students.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun confirmDelete(student: Student) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa sinh viên")
            .setMessage("Bạn có chắc muốn xóa ${student.fullName}?")
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.delete(student)
            }
            .show()
    }

    override fun onDestroyView() {
        binding.rvStudents.adapter = null
        _binding = null
        super.onDestroyView()
    }
}