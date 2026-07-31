package com.example.data.model

import com.example.data.local.RecipeEntity
import com.example.data.local.RecipeIngredientEntity

/**
 * Model class wrapping a recipe with ingredient inventory matching status.
 */
data class RecipeWithMatch(
    val recipe: RecipeEntity,
    val ingredients: List<RecipeIngredientEntity>,
    val inStockIngredientsCount: Int,
    val requiredIngredientsCount: Int,
    val missingIngredientNames: List<String>,
    val isFullyMatch: Boolean
) {
    val matchPercentage: Int
        get() = if (requiredIngredientsCount == 0) 100
        else ((inStockIngredientsCount.toFloat() / requiredIngredientsCount) * 100).toInt()
}

/**
 * Enum for match filtering options.
 */
enum class MatchFilter(val label: String) {
    ALL("All Recipes"),
    CAN_MAKE("Can Make Now"),
    NEARLY_THERE("Missing 1 Ingredient")
}

/**
 * Ingredient category constants.
 */
object IngredientCategories {
    val ALL_CATEGORIES = listOf(
        "All",
        "Spirits",
        "Liqueurs & Fortified",
        "Mixers & Juices",
        "Citrus & Produce",
        "Bitters & Syrups",
        "Garnishes & Ice",
        "Other"
    )
}
