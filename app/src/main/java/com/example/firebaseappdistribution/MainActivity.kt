package com.example.firebaseappdistribution

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.firebaseappdistribution.data.NoteRepository
import com.example.firebaseappdistribution.data.NotesDatabase
import com.example.firebaseappdistribution.ui.NotesNavigation
import com.example.firebaseappdistribution.ui.theme.FirebaseAppDistributionTheme
import com.example.firebaseappdistribution.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize database and repository
        val database = NotesDatabase.getDatabase(applicationContext)
        val repository = NoteRepository(database.noteDao())
        val viewModel = NotesViewModel(repository)
        
        setContent {
            FirebaseAppDistributionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotesNavigation(viewModel = viewModel)
                }
            }
        }
    }
}