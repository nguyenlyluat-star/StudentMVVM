
package com.ute.studentmvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ute.studentmvvm.ui.StudentListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container_view,
                    StudentListFragment()
                )
                .commit()
        }
    }
}
