package com.cocktails.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * App Room Database holding ingredients, recipes, and recipe ingredients.
 */
@Database(
    entities = [
        IngredientEntity::class,
        RecipeEntity::class,
        RecipeIngredientEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cocktailDao(): CocktailDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cocktail_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.cocktailDao())
                    }
                }
            }
        }

        /**
         * Pre-populates the database with initial classic cocktail recipes and generic ingredients.
         */
        suspend fun populateDatabase(dao: CocktailDao) {
            if (dao.getRecipeCount() > 0) return

            // 1. Collect all unique ingredients
            val allIngredients = DefaultData.PREPOPULATED_RECIPES.flatMap { recipe ->
                recipe.ingredients.map { ing ->
                    IngredientEntity(
                        name = ing.name,
                        category = ing.category,
                        inStock = false,
                        isCustom = false
                    )
                }
            }.distinctBy { it.name.lowercase() }

            dao.insertIngredients(allIngredients)

            // 2. Insert recipes and recipe-ingredient links
            DefaultData.PREPOPULATED_RECIPES.forEach { recipe ->
                val recipeId = dao.insertRecipe(
                    RecipeEntity(
                        name = recipe.name,
                        description = recipe.description,
                        glassType = recipe.glassType,
                        ice = recipe.ice,
                        garnish = recipe.garnish,
                        instructions = recipe.instructions,
                        baseSpirit = recipe.baseSpirit,
                        isFavorite = false,
                        isCustom = false
                    )
                ).toInt()

                val recipeIngredients = recipe.ingredients.map { ing ->
                    RecipeIngredientEntity(
                        recipeId = recipeId,
                        ingredientName = ing.name,
                        amount = ing.amount,
                        isOptional = ing.isOptional
                    )
                }

                dao.insertRecipeIngredients(recipeIngredients)
            }
        }
    }
}
