package com.example.firebaseappdistribution.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.firebaseappdistribution.data.Note
import com.example.firebaseappdistribution.ui.theme.NoteColors
import com.example.firebaseappdistribution.viewmodel.NotesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    viewModel: NotesViewModel,
    noteId: Int?,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableStateOf(NoteColors.getRandomColorIndex()) }
    val scope = rememberCoroutineScope()
    
    // Load note if editing
    LaunchedEffect(noteId) {
        noteId?.let {
            viewModel.getNoteById(it)?.let { note ->
                title = note.title
                content = note.content
                selectedColorIndex = note.backgroundColor
            }
        }
    }
    
    val backgroundColor = NoteColors.getColor(selectedColorIndex)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (noteId == null) "Add Note" else "Edit Note",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (title.isNotBlank() || content.isNotBlank()) {
                                scope.launch {
                                    if (noteId == null) {
                                        viewModel.insertNote(
                                            Note(
                                                title = title.ifBlank { "Untitled" },
                                                content = content,
                                                backgroundColor = selectedColorIndex
                                            )
                                        )
                                    } else {
                                        viewModel.updateNote(
                                            Note(
                                                id = noteId,
                                                title = title.ifBlank { "Untitled" },
                                                content = content,
                                                backgroundColor = selectedColorIndex
                                            )
                                        )
                                    }
                                    onNavigateBack()
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save Note")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Color Picker Section
            ColorPickerSection(
                selectedColorIndex = selectedColorIndex,
                onColorSelected = { selectedColorIndex = it },
                onRandomColor = { selectedColorIndex = NoteColors.getRandomColorIndex() }
            )
            
            // Note Content Section with selected background color
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor.copy(alpha = 0.3f))
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = Color.White.copy(alpha = 0.7f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    maxLines = Int.MAX_VALUE,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = Color.White.copy(alpha = 0.7f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
fun ColorPickerSection(
    selectedColorIndex: Int,
    onColorSelected: (Int) -> Unit,
    onRandomColor: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Note Color",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                // Random Color Button
                FilledTonalButton(
                    onClick = onRandomColor,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Random Color",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Random", style = MaterialTheme.typography.labelMedium)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Color Options Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                itemsIndexed(NoteColors.noteColors) { index, color ->
                    ColorOption(
                        color = color,
                        isSelected = index == selectedColorIndex,
                        colorName = NoteColors.colorNames[index],
                        onClick = { onColorSelected(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun ColorOption(
    color: Color,
    isSelected: Boolean,
    colorName: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                    shape = CircleShape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = colorName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
