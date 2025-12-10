package com.example.firebaseappdistribution.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Predefined color palette for note backgrounds
 * Each color is designed to be visually appealing and provide good contrast with text
 */
object NoteColors {
    
    // Predefined note background colors - vibrant and modern
    val noteColors = listOf(
        Color(0xFFFFF9C4), // Amber/Yellow - Warm and inviting
        Color(0xFFC8E6C9), // Mint Green - Fresh and calming
        Color(0xFFBBDEFB), // Sky Blue - Peaceful and serene
        Color(0xFFE1BEE7), // Lavender Purple - Creative and elegant
        Color(0xFFF8BBD9), // Blush Pink - Soft and playful
        Color(0xFFFFE0B2), // Peach Orange - Warm and friendly
        Color(0xFFECEFF1), // Light Gray - Neutral and clean
        Color(0xFFB2DFDB)  // Teal - Refreshing and modern
    )
    
    // Color names for accessibility
    val colorNames = listOf(
        "Amber",
        "Mint",
        "Sky Blue",
        "Lavender",
        "Blush Pink",
        "Peach",
        "Light Gray",
        "Teal"
    )
    
    /**
     * Get a color by its index
     * @param index The index of the color (0-7)
     * @return The Color at the given index, or the first color if index is out of bounds
     */
    fun getColor(index: Int): Color {
        return noteColors.getOrElse(index) { noteColors[0] }
    }
    
    /**
     * Get a random color index
     * @return A random index between 0 and the number of colors - 1
     */
    fun getRandomColorIndex(): Int {
        return Random.nextInt(noteColors.size)
    }
    
    /**
     * Get the text color that provides good contrast with the background
     * @param backgroundColorIndex The index of the background color
     * @return A dark color for text that contrasts well with the background
     */
    fun getTextColor(backgroundColorIndex: Int): Color {
        // All our pastel backgrounds work well with dark text
        return Color(0xFF1A1A1A)
    }
    
    /**
     * Get a slightly darker version of the text color for secondary text
     * @param backgroundColorIndex The index of the background color
     * @return A medium-dark color for secondary text
     */
    fun getSecondaryTextColor(backgroundColorIndex: Int): Color {
        return Color(0xFF424242)
    }
}
