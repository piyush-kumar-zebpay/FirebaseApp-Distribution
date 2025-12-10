package com.example.firebaseappdistribution.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.firebaseappdistribution.data.Note
import com.example.firebaseappdistribution.ui.theme.NoteColors
import com.example.firebaseappdistribution.viewmodel.NotesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    viewModel: NotesViewModel,
    onNavigateToAddEdit: (Int?) -> Unit
) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Notes", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEdit(null) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📝",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No notes yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap the + button to create your first note",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteItem(
                        note = note,
                        onNoteClick = { onNavigateToAddEdit(note.id) },
                        onDeleteClick = { viewModel.deleteNote(note) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(
    note: Note,
    onNoteClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val backgroundColor = NoteColors.getColor(note.backgroundColor)
    val textColor = NoteColors.getTextColor(note.backgroundColor)
    val secondaryTextColor = NoteColors.getSecondaryTextColor(note.backgroundColor)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNoteClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        // Color accent bar at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(backgroundColor.copy(alpha = 0.8f))
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = secondaryTextColor.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = formatDate(note.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryTextColor.copy(alpha = 0.6f)
                )
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Edit button
                IconButton(
                    onClick = onNoteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Note",
                        tint = textColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Delete button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Note",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
