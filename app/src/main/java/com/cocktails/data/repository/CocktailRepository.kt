package com.cocktails.data.repository

import com.cocktails.data.local.CocktailDao
import com.cocktails.data.local.IngredientEntity
import com.cocktails.data.local.RecipeEntity
import com.cocktails.data.local.RecipeIngredientEntity
import com.cocktails.data.local.AppDatabase
import com.cocktails.data.model.RecipeWithMatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Custom ingredient input structure for creating recipes.
 */
data class CustomIngredientInput(
    val name: String,
    val amount: String,
    val category: String = "Other",
    val isOptional: Boolean = false
)

/**
 * Repository for managing cocktail ingredients, recipe matches, and custom recipes.
 */
class CocktailRepository(private val dao: CocktailDao) {

    /**
     * Flow of all ingredients ordered by category and name.
     */
    val allIngredients: Flow<List<IngredientEntity>> = dao.getAllIngredients()

    /**
     * Flow of all recipes combined with user's inventory stock status.
     * Evaluates custom and pre-populated recipes alike.
     */
    val recipesWithMatch: Flow<List<RecipeWithMatch>> = combine(
        dao.getAllRecipes(),
        dao.getAllRecipeIngredients(),
        dao.getAllIngredients()
    ) { recipes, recipeIngredientsMap, ingredients ->
        val inStockSet = ingredients
            .filter { it.inStock }
            .map { it.name.trim().lowercase() }
            .toSet()

        recipes.map { recipe ->
            val ingredientsForRecipe = recipeIngredientsMap.filter { it.recipeId == recipe.id }
            
            // Required ingredients (non-optional)
            val requiredIngredients = ingredientsForRecipe.filter { !it.isOptional }
            
            val matchingInStock = requiredIngredients.count { ing ->
                inStockSet.contains(ing.ingredientName.trim().lowercase())
            }

            val missingIngredients = requiredIngredients.filterNot { ing ->
                inStockSet.contains(ing.ingredientName.trim().lowercase())
            }.map { it.ingredientName }

            val totalRequired = requiredIngredients.size
            val isFullyMatch = (totalRequired > 0 && matchingInStock == totalRequired) || totalRequired == 0

            RecipeWithMatch(
                recipe = recipe,
                ingredients = ingredientsForRecipe,
                inStockIngredientsCount = matchingInStock,
                requiredIngredientsCount = totalRequired,
                missingIngredientNames = missingIngredients,
                isFullyMatch = isFullyMatch
            )
        }
    }

    suspend fun toggleIngredientStock(ingredientId: Int, inStock: Boolean) {
        dao.updateIngredientStock(ingredientId, inStock)
    }

    suspend fun setAllIngredientsStock(inStock: Boolean) {
        dao.updateAllIngredientsStock(inStock)
    }

    suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        dao.setRecipeFavorite(recipeId, isFavorite)
    }

    suspend fun deleteCustomRecipe(recipeId: Int) {
        dao.deleteRecipeIngredients(recipeId)
        dao.deleteRecipe(recipeId)
    }

    /**
     * Updates an existing custom recipe.
     */
    suspend fun updateCustomRecipe(
        recipeId: Int,
        name: String,
        description: String,
        glassType: String,
        ice: String,
        garnish: String,
        instructions: String,
        baseSpirit: String,
        isFavorite: Boolean,
        ingredientsInput: List<CustomIngredientInput>
    ) {
        // 1. Check & insert any new generic ingredients into the database
        ingredientsInput.forEach { input ->
            val trimmedName = input.name.trim()
            if (trimmedName.isNotEmpty()) {
                val existing = dao.findIngredientByName(trimmedName)
                if (existing == null) {
                    dao.insertIngredient(
                        IngredientEntity(
                            name = trimmedName,
                            category = input.category.ifBlank { "Other" },
                            inStock = false,
                            isCustom = true
                        )
                    )
                }
            }
        }

        // 2. Update the recipe entity
        dao.insertRecipe(
            RecipeEntity(
                id = recipeId,
                name = name.trim(),
                description = description.trim(),
                glassType = glassType.ifBlank { "Rocks Glass" },
                ice = ice.ifBlank { "Cubed Ice" },
                garnish = garnish.ifBlank { "None" },
                instructions = instructions.trim(),
                baseSpirit = baseSpirit.ifBlank { "Custom Spirit" },
                isFavorite = isFavorite,
                isCustom = true
            )
        )

        // 3. Delete old ingredient mappings and insert new ones
        dao.deleteRecipeIngredients(recipeId)

        val recipeIngredients = ingredientsInput
            .filter { it.name.trim().isNotEmpty() }
            .map { input ->
                RecipeIngredientEntity(
                    recipeId = recipeId,
                    ingredientName = input.name.trim(),
                    amount = input.amount.trim().ifBlank { "1 oz" },
                    isOptional = input.isOptional
                )
            }

        dao.insertRecipeIngredients(recipeIngredients)
    }

    /**
     * Creates a custom recipe. Automatically checks if ingredients exist in database,
     * and if not, adds new generic ingredients to the database so they appear in inventory.
     */
    suspend fun createCustomRecipe(
        name: String,
        description: String,
        glassType: String,
        ice: String,
        garnish: String,
        instructions: String,
        baseSpirit: String,
        ingredientsInput: List<CustomIngredientInput>
    ): Long {
        // 1. Check & insert any new generic ingredients into the database
        ingredientsInput.forEach { input ->
            val trimmedName = input.name.trim()
            if (trimmedName.isNotEmpty()) {
                val existing = dao.findIngredientByName(trimmedName)
                if (existing == null) {
                    dao.insertIngredient(
                        IngredientEntity(
                            name = trimmedName,
                            category = input.category.ifBlank { "Other" },
                            inStock = false,
                            isCustom = true
                        )
                    )
                }
            }
        }

        // 2. Insert the recipe
        val recipeId = dao.insertRecipe(
            RecipeEntity(
                name = name.trim(),
                description = description.trim(),
                glassType = glassType.ifBlank { "Rocks Glass" },
                ice = ice.ifBlank { "Cubed Ice" },
                garnish = garnish.ifBlank { "None" },
                instructions = instructions.trim(),
                baseSpirit = baseSpirit.ifBlank { "Custom Spirit" },
                isFavorite = false,
                isCustom = true
            )
        ).toInt()

        // 3. Insert recipe ingredient mappings
        val recipeIngredients = ingredientsInput
            .filter { it.name.trim().isNotEmpty() }
            .map { input ->
                RecipeIngredientEntity(
                    recipeId = recipeId,
                    ingredientName = input.name.trim(),
                    amount = input.amount.trim().ifBlank { "1 oz" },
                    isOptional = input.isOptional
                )
            }

        dao.insertRecipeIngredients(recipeIngredients)
        return recipeId.toLong()
    }

    /**
     * Exports custom recipes and custom ingredients as JSON string.
     */
    suspend fun exportCustomBackupJson(): String {
        val customRecipes = dao.getCustomRecipesList()
        val allRecipeIngredients = dao.getAllRecipeIngredientsList()
        val customIngredients = dao.getCustomIngredientsList()
        return com.cocktails.util.BackupJsonManager.exportToJson(
            customRecipes = customRecipes,
            recipeIngredientsMap = allRecipeIngredients,
            customIngredients = customIngredients
        )
    }

    /**
     * Exports all recipes and all ingredients as JSON string.
     */
    suspend fun exportAllBackupJson(): String {
        val allRecipes = dao.getAllRecipesList()
        val allRecipeIngredients = dao.getAllRecipeIngredientsList()
        val allIngredients = dao.getAllIngredientsList()
        return com.cocktails.util.BackupJsonManager.exportToJson(
            customRecipes = allRecipes,
            recipeIngredientsMap = allRecipeIngredients,
            customIngredients = allIngredients
        )
    }

    /**
     * Imports custom recipes and ingredients from a JSON string into Room database.
     */
    suspend fun importBackupJson(jsonString: String): com.cocktails.util.ImportResult {
        return try {
            val (ingredients, recipes) = com.cocktails.util.BackupJsonManager.parseBackupJson(jsonString)
            var importedIngCount = 0
            var importedRecipeCount = 0

            // 1. Insert ingredients
            ingredients.forEach { ing ->
                val existing = dao.findIngredientByName(ing.name)
                if (existing == null) {
                    dao.insertIngredient(ing)
                    importedIngCount++
                }
            }

            // 2. Insert recipes
            recipes.forEach { recipe ->
                recipe.ingredients.forEach { rIng ->
                    val existing = dao.findIngredientByName(rIng.ingredientName)
                    if (existing == null) {
                        dao.insertIngredient(
                            IngredientEntity(
                                name = rIng.ingredientName,
                                category = "Other",
                                inStock = false,
                                isCustom = true
                            )
                        )
                        importedIngCount++
                    }
                }

                val newRecipeId = dao.insertRecipe(
                    RecipeEntity(
                        name = recipe.name,
                        description = recipe.description,
                        glassType = recipe.glassType,
                        ice = recipe.ice,
                        garnish = recipe.garnish,
                        instructions = recipe.instructions,
                        baseSpirit = recipe.baseSpirit,
                        isFavorite = recipe.isFavorite,
                        isCustom = true
                    )
                ).toInt()

                val recipeIngEntities = recipe.ingredients.map { rIng ->
                    RecipeIngredientEntity(
                        recipeId = newRecipeId,
                        ingredientName = rIng.ingredientName,
                        amount = rIng.amount,
                        isOptional = rIng.isOptional
                    )
                }
                dao.insertRecipeIngredients(recipeIngEntities)
                importedRecipeCount++
            }

            com.cocktails.util.ImportResult(
                importedRecipesCount = importedRecipeCount,
                importedIngredientsCount = importedIngCount
            )
        } catch (e: Exception) {
            com.cocktails.util.ImportResult(0, 0, e.message ?: "Failed to parse backup JSON file.")
        }
    }

    suspend fun ensureDatabasePopulated() {
        if (dao.getRecipeCount() == 0) {
            AppDatabase.populateDatabase(dao)
        }
    }
}
