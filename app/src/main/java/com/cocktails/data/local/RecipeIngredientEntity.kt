package com.cocktails.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity mapping ingredients to a recipe with required amounts.
 */
@Entity(
    tableName = "recipe_ingredients",
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["recipeId"])]
)
data class RecipeIngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val ingredientName: String,
    val amount: String,
    val isOptional: Boolean = false
)
