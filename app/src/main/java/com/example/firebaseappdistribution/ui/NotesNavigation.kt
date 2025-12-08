package com.example.firebaseappdistribution.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.firebaseappdistribution.ui.screens.AddEditNoteScreen
import com.example.firebaseappdistribution.ui.screens.NotesListScreen
import com.example.firebaseappdistribution.viewmodel.NotesViewModel

@Composable
fun NotesNavigation(viewModel: NotesViewModel) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "notes_list"
    ) {
        composable("notes_list") {
            NotesListScreen(
                viewModel = viewModel,
                onNavigateToAddEdit = { noteId ->
                    if (noteId == null) {
                        navController.navigate("add_edit_note")
                    } else {
                        navController.navigate("add_edit_note/$noteId")
                    }
                }
            )
        }
        
        composable(
            route = "add_edit_note?noteId={noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val noteIdString = backStackEntry.arguments?.getString("noteId")
            val noteId = noteIdString?.toIntOrNull()
            
            AddEditNoteScreen(
                viewModel = viewModel,
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
