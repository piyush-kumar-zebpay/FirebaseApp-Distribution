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
                        navController.navigate("add_edit_note/-1")
                    } else {
                        navController.navigate("add_edit_note/$noteId")
                    }
                }
            )
        }
        
        // Route for adding new note (no noteId)
        composable("add_edit_note/-1") {
            AddEditNoteScreen(
                viewModel = viewModel,
                noteId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Route for editing existing note (with noteId)
        composable(
            route = "add_edit_note/{noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId")
            
            AddEditNoteScreen(
                viewModel = viewModel,
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
