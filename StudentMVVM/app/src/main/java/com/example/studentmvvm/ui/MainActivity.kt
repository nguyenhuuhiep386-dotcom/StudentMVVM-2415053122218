package com.example.studentmvvm.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.studentmvvm.R
import com.example.studentmvvm.data.AppDatabase
import com.example.studentmvvm.databinding.ActivityMainBinding
import com.example.studentmvvm.network.RetrofitClient
import com.example.studentmvvm.repository.StudentRepository
import com.example.studentmvvm.viewmodel.StudentViewModel
import com.example.studentmvvm.viewmodel.StudentViewModelFactory

class MainActivity : AppCompatActivity() {
    private val repository by lazy {
        StudentRepository(AppDatabase.getDatabase(this), RetrofitClient.apiService)
    }

    val studentViewModel: StudentViewModel by viewModels {
        StudentViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val safeArea = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.setPadding(safeArea.left, safeArea.top, safeArea.right, safeArea.bottom)
            WindowInsetsCompat.CONSUMED
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, StudentListFragment())
                .commit()
        }
    }
}
