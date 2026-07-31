package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a cocktail recipe.
 */
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val glassType: String,
    val ice: String = "Cubed Ice",
    val garnish: String = "None",
    val instructions: String,
    val baseSpirit: String,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)
