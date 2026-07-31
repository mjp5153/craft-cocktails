package com.mjp5153.craft.cocktails.util

import com.mjp5153.craft.cocktails.data.local.IngredientEntity
import com.mjp5153.craft.cocktails.data.local.RecipeEntity
import com.mjp5153.craft.cocktails.data.local.RecipeIngredientEntity
import org.json.JSONArray
import org.json.JSONObject

data class ImportResult(
    val importedRecipesCount: Int,
    val importedIngredientsCount: Int,
    val errorMessage: String? = null
)

data class ParsedRecipeIngredient(
    val ingredientName: String,
    val amount: String,
    val isOptional: Boolean
)

data class ParsedRecipe(
    val name: String,
    val description: String,
    val glassType: String,
    val ice: String,
    val garnish: String,
    val instructions: String,
    val baseSpirit: String,
    val isFavorite: Boolean,
    val ingredients: List<ParsedRecipeIngredient>
)

object BackupJsonManager {

    /**
     * Serializes recipes and ingredients into a clean JSON string.
     */
    fun exportToJson(
        customRecipes: List<RecipeEntity>,
        recipeIngredientsMap: List<RecipeIngredientEntity>,
        customIngredients: List<IngredientEntity>
    ): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("app", "CraftCocktails")
        root.put("exportedAt", System.currentTimeMillis())

        // Custom Ingredients Array
        val ingredientsArray = JSONArray()
        customIngredients.forEach { ing ->
            val ingObj = JSONObject()
            ingObj.put("name", ing.name)
            ingObj.put("category", ing.category)
            ingObj.put("inStock", ing.inStock)
            ingObj.put("isCustom", ing.isCustom)
            ingredientsArray.put(ingObj)
        }
        root.put("customIngredients", ingredientsArray)

        // Custom Recipes Array
        val recipesArray = JSONArray()
        customRecipes.forEach { recipe ->
            val recipeObj = JSONObject()
            recipeObj.put("name", recipe.name)
            recipeObj.put("description", recipe.description)
            recipeObj.put("baseSpirit", recipe.baseSpirit)
            recipeObj.put("glassType", recipe.glassType)
            recipeObj.put("ice", recipe.ice)
            recipeObj.put("garnish", recipe.garnish)
            recipeObj.put("instructions", recipe.instructions)
            recipeObj.put("isFavorite", recipe.isFavorite)
            recipeObj.put("isCustom", recipe.isCustom)

            // Recipe Ingredients
            val ingredientsForRecipe = recipeIngredientsMap.filter { it.recipeId == recipe.id }
            val recipeIngArray = JSONArray()
            ingredientsForRecipe.forEach { rIng ->
                val rIngObj = JSONObject()
                rIngObj.put("ingredientName", rIng.ingredientName)
                rIngObj.put("amount", rIng.amount)
                rIngObj.put("isOptional", rIng.isOptional)
                recipeIngArray.put(rIngObj)
            }
            recipeObj.put("ingredients", recipeIngArray)

            recipesArray.put(recipeObj)
        }
        root.put("customRecipes", recipesArray)

        return root.toString(2)
    }

    /**
     * Parses JSON backup content into lists of ingredients and recipes.
     */
    fun parseBackupJson(jsonString: String): Pair<List<IngredientEntity>, List<ParsedRecipe>> {
        val root = JSONObject(jsonString)

        val ingredientsList = mutableListOf<IngredientEntity>()
        val ingArray = when {
            root.has("customIngredients") -> root.getJSONArray("customIngredients")
            root.has("ingredients") -> root.getJSONArray("ingredients")
            else -> JSONArray()
        }

        for (i in 0 until ingArray.length()) {
            val obj = ingArray.getJSONObject(i)
            val name = obj.optString("name", "").trim()
            if (name.isNotEmpty()) {
                ingredientsList.add(
                    IngredientEntity(
                        name = name,
                        category = obj.optString("category", "Other"),
                        inStock = obj.optBoolean("inStock", false),
                        isCustom = obj.optBoolean("isCustom", true)
                    )
                )
            }
        }

        val recipesList = mutableListOf<ParsedRecipe>()
        val recipesArray = when {
            root.has("customRecipes") -> root.getJSONArray("customRecipes")
            root.has("recipes") -> root.getJSONArray("recipes")
            else -> JSONArray()
        }

        for (i in 0 until recipesArray.length()) {
            val obj = recipesArray.getJSONObject(i)
            val name = obj.optString("name", "").trim()
            if (name.isNotEmpty()) {
                val parsedIngredients = mutableListOf<ParsedRecipeIngredient>()
                if (obj.has("ingredients")) {
                    val ingArr = obj.getJSONArray("ingredients")
                    for (j in 0 until ingArr.length()) {
                        val ingObj = ingArr.getJSONObject(j)
                        val ingName = ingObj.optString("ingredientName", ingObj.optString("name", "")).trim()
                        if (ingName.isNotEmpty()) {
                            parsedIngredients.add(
                                ParsedRecipeIngredient(
                                    ingredientName = ingName,
                                    amount = ingObj.optString("amount", "1 oz"),
                                    isOptional = ingObj.optBoolean("isOptional", false)
                                )
                            )
                        }
                    }
                }

                recipesList.add(
                    ParsedRecipe(
                        name = name,
                        description = obj.optString("description", ""),
                        glassType = obj.optString("glassType", "Rocks Glass"),
                        ice = obj.optString("ice", "Cubed Ice"),
                        garnish = obj.optString("garnish", "None"),
                        instructions = obj.optString("instructions", ""),
                        baseSpirit = obj.optString("baseSpirit", "Custom Spirit"),
                        isFavorite = obj.optBoolean("isFavorite", false),
                        ingredients = parsedIngredients
                    )
                )
            }
        }

        return Pair(ingredientsList, recipesList)
    }
}
