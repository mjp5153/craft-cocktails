package com.mjp5153.craft.cocktails.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a cocktail ingredient.
 * Ingredients are kept generic (no brand names).
 */
@Entity(
    tableName = "ingredients",
    indices = [Index(value = ["name"], unique = true)]
)
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // e.g., "Spirits", "Mixers & Juices", "Citrus & Fruits", "Bitters & Syrups", "Liqueurs", "Garnishes & Ice", "Other"
    val inStock: Boolean = false,
    val isCustom: Boolean = false
)
