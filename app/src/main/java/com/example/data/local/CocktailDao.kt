package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ingredients and cocktail recipes.
 */
@Dao
interface CocktailDao {

    // --- Ingredients ---

    @Query("SELECT * FROM ingredients ORDER BY category ASC, name ASC")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredients WHERE inStock = 1")
    fun getInStockIngredients(): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredients WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun findIngredientByName(name: String): IngredientEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIngredient(ingredient: IngredientEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Update
    suspend fun updateIngredient(ingredient: IngredientEntity)

    @Query("UPDATE ingredients SET inStock = :inStock WHERE id = :id")
    suspend fun updateIngredientStock(id: Int, inStock: Boolean)

    @Query("UPDATE ingredients SET inStock = :inStock")
    suspend fun updateAllIngredientsStock(inStock: Boolean)

    @Query("SELECT * FROM ingredients WHERE isCustom = 1 ORDER BY category ASC, name ASC")
    suspend fun getCustomIngredientsList(): List<IngredientEntity>

    @Query("SELECT * FROM ingredients ORDER BY category ASC, name ASC")
    suspend fun getAllIngredientsList(): List<IngredientEntity>

    // --- Recipes ---

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isCustom = 1 ORDER BY name ASC")
    suspend fun getCustomRecipesList(): List<RecipeEntity>

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    suspend fun getAllRecipesList(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: Int): Flow<RecipeEntity?>

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setRecipeFavorite(id: Int, isFavorite: Boolean)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipe(id: Int)

    // --- Recipe Ingredients ---

    @Query("SELECT * FROM recipe_ingredients")
    fun getAllRecipeIngredients(): Flow<List<RecipeIngredientEntity>>

    @Query("SELECT * FROM recipe_ingredients")
    suspend fun getAllRecipeIngredientsList(): List<RecipeIngredientEntity>

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    fun getIngredientsForRecipe(recipeId: Int): Flow<List<RecipeIngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredients(ingredients: List<RecipeIngredientEntity>)

    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun deleteRecipeIngredients(recipeId: Int)
}
