
package com.ute.studentmvvm.network

import com.google.gson.annotations.SerializedName
import com.ute.studentmvvm.model.Student

data class StudentDto(
    val id: Int = 0,

    @SerializedName("name")
    val fullName: String = "",

    val username: String = "",
    val email: String = ""
)

fun StudentDto.toEntity(): Student {
    return Student(
        fullName = fullName,
        studentCode = "API$id",
        email = email,
        gpa = 0.0,
        major = "Dữ liệu API mẫu"
    )
}
