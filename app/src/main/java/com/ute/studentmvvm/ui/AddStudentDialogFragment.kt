package com.ute.studentmvvm.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ute.studentmvvm.data.AppDatabase
import com.ute.studentmvvm.databinding.DialogAddStudentBinding
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.repository.StudentRepository
import com.ute.studentmvvm.viewmodel.StudentViewModel
import com.ute.studentmvvm.viewmodel.StudentViewModelFactory

class AddStudentDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_NAME = "name"
        private const val ARG_CODE = "code"
        private const val ARG_EMAIL = "email"
        private const val ARG_GPA = "gpa"
        private const val ARG_MAJOR = "major"

        fun newInstance(student: Student? = null): AddStudentDialogFragment {
            return AddStudentDialogFragment().apply {
                arguments = Bundle().apply {
                    if (student != null) {
                        putInt(ARG_ID, student.id)
                        putString(ARG_NAME, student.fullName)
                        putString(ARG_CODE, student.studentCode)
                        putString(ARG_EMAIL, student.email)
                        putDouble(ARG_GPA, student.gpa)
                        putString(ARG_MAJOR, student.major)
                    }
                }
            }
        }
    }

    private val viewModel: StudentViewModel by activityViewModels {
        val database = AppDatabase.getDatabase(requireContext())
        StudentViewModelFactory(
            StudentRepository(database.studentDao())
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddStudentBinding.inflate(layoutInflater)

        val args = requireArguments()
        val isEdit = args.containsKey(ARG_ID)

        if (isEdit) {
            binding.tvDialogTitle.text = "Chỉnh sửa sinh viên"
            binding.edtFullName.setText(args.getString(ARG_NAME))
            binding.edtStudentCode.setText(args.getString(ARG_CODE))
            binding.edtEmail.setText(args.getString(ARG_EMAIL))
            binding.edtGpa.setText(args.getDouble(ARG_GPA).toString())
            binding.edtMajor.setText(args.getString(ARG_MAJOR))
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setNegativeButton("Hủy", null)
            .setPositiveButton(if (isEdit) "Lưu" else "Thêm", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = binding.edtFullName.text.toString().trim()
                val code = binding.edtStudentCode.text.toString().trim()
                val email = binding.edtEmail.text.toString().trim()
                val gpaText = binding.edtGpa.text.toString().trim()
                val major = binding.edtMajor.text.toString().trim()
                val gpa = gpaText.toDoubleOrNull()

                when {
                    name.isBlank() || code.isBlank() ||
                            email.isBlank() || major.isBlank() -> {
                        Toast.makeText(
                            requireContext(),
                            "Vui lòng nhập đầy đủ thông tin",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        binding.edtEmail.error = "Email không hợp lệ"
                    }

                    gpa == null || !gpa.isFinite() || gpa !in 0.0..4.0 -> {
                        binding.edtGpa.error = "GPA phải từ 0 đến 4"
                    }

                    else -> {
                        val student = Student(
                            id = if (isEdit) args.getInt(ARG_ID) else 0,
                            fullName = name,
                            studentCode = code,
                            email = email,
                            gpa = gpa,
                            major = major
                        )

                        if (isEdit) {
                            viewModel.update(student)
                        } else {
                            viewModel.insert(student)
                        }

                        dialog.dismiss()
                    }
                }
            }
        }

        return dialog
    }
}