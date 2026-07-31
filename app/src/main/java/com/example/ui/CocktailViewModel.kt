package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.IngredientEntity
import com.example.data.model.MatchFilter
import com.example.data.model.RecipeWithMatch
import com.example.data.repository.CocktailRepository
import com.example.data.repository.CustomIngredientInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Filter mode for ingredients tab.
 */
enum class StockFilter(val label: String) {
    ALL("All Ingredients"),
    IN_BAR("In My Bar"),
    NEEDED("Not In Bar")
}

/**
 * Tabs for recipes screen.
 */
enum class RecipeTab(val label: String) {
    RECOMMENDED("Recommendations"),
    ALL("All Recipes"),
    FAVORITES("Favorites"),
    MY_CUSTOM("My Recipes")
}

class CocktailViewModel(private val repository: CocktailRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.ensureDatabasePopulated()
        }
    }

    // --- Search & Filter States ---
    private val _ingredientSearchQuery = MutableStateFlow("")
    val ingredientSearchQuery: StateFlow<String> = _ingredientSearchQuery.asStateFlow()

    private val _selectedIngredientCategory = MutableStateFlow("All")
    val selectedIngredientCategory: StateFlow<String> = _selectedIngredientCategory.asStateFlow()

    private val _stockFilter = MutableStateFlow(StockFilter.ALL)
    val stockFilter: StateFlow<StockFilter> = _stockFilter.asStateFlow()

    private val _recipeSearchQuery = MutableStateFlow("")
    val recipeSearchQuery: StateFlow<String> = _recipeSearchQuery.asStateFlow()

    private val _selectedRecipeTab = MutableStateFlow(RecipeTab.RECOMMENDED)
    val selectedRecipeTab: StateFlow<RecipeTab> = _selectedRecipeTab.asStateFlow()

    private val _selectedBaseSpirit = MutableStateFlow("All")
    val selectedBaseSpirit: StateFlow<String> = _selectedBaseSpirit.asStateFlow()

    private val _selectedMatchFilter = MutableStateFlow(MatchFilter.ALL)
    val selectedMatchFilter: StateFlow<MatchFilter> = _selectedMatchFilter.asStateFlow()

    // --- Raw Flow Sources ---
    val allIngredients: StateFlow<List<IngredientEntity>> = repository.allIngredients
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allRecipesWithMatch: StateFlow<List<RecipeWithMatch>> = repository.recipesWithMatch
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Derived Filtered Flow for Ingredients Tab ---
    val filteredIngredients: StateFlow<List<IngredientEntity>> = combine(
        allIngredients,
        _ingredientSearchQuery,
        _selectedIngredientCategory,
        _stockFilter
    ) { ingredients, query, category, stock ->
        ingredients.filter { ing ->
            val matchesQuery = query.isBlank() || ing.name.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || ing.category.equals(category, ignoreCase = true)
            val matchesStock = when (stock) {
                StockFilter.ALL -> true
                StockFilter.IN_BAR -> ing.inStock
                StockFilter.NEEDED -> !ing.inStock
            }
            matchesQuery && matchesCategory && matchesStock
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Derived Filtered Flow for Recipes Tab ---
    val filteredRecipes: StateFlow<List<RecipeWithMatch>> = combine(
        allRecipesWithMatch,
        _recipeSearchQuery,
        _selectedRecipeTab,
        _selectedBaseSpirit,
        _selectedMatchFilter
    ) { recipes, query, tab, spirit, matchFilter ->
        recipes.filter { r ->
            val recipe = r.recipe
            val matchesQuery = query.isBlank() ||
                    recipe.name.contains(query, ignoreCase = true) ||
                    recipe.baseSpirit.contains(query, ignoreCase = true) ||
                    r.ingredients.any { it.ingredientName.contains(query, ignoreCase = true) }

            val matchesTab = when (tab) {
                RecipeTab.RECOMMENDED -> r.inStockIngredientsCount > 0
                RecipeTab.ALL -> true
                RecipeTab.FAVORITES -> recipe.isFavorite
                RecipeTab.MY_CUSTOM -> recipe.isCustom
            }

            val matchesSpirit = spirit == "All" || recipe.baseSpirit.equals(spirit, ignoreCase = true)

            val matchesMatchFilter = when (matchFilter) {
                MatchFilter.ALL -> true
                MatchFilter.CAN_MAKE -> r.isFullyMatch
                MatchFilter.NEARLY_THERE -> r.missingIngredientNames.size == 1
            }

            matchesQuery && matchesTab && matchesSpirit && matchesMatchFilter
        }.sortedWith(
            compareByDescending<RecipeWithMatch> { it.isFullyMatch }
                .thenByDescending { it.matchPercentage }
                .thenBy { it.recipe.name }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Stats Summary ---
    val inBarCount: StateFlow<Int> = combine(allIngredients) { list ->
        list.firstOrNull()?.count { it.inStock } ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val readyToMakeCount: StateFlow<Int> = combine(allRecipesWithMatch) { recipes ->
        recipes.firstOrNull()?.count { it.isFullyMatch } ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- User Action Handlers ---

    fun onIngredientSearchQueryChange(query: String) {
        _ingredientSearchQuery.value = query
    }

    fun onIngredientCategorySelect(category: String) {
        _selectedIngredientCategory.value = category
    }

    fun onStockFilterSelect(filter: StockFilter) {
        _stockFilter.value = filter
    }

    fun onRecipeSearchQueryChange(query: String) {
        _recipeSearchQuery.value = query
    }

    fun onRecipeTabSelect(tab: RecipeTab) {
        _selectedRecipeTab.value = tab
    }

    fun onBaseSpiritSelect(spirit: String) {
        _selectedBaseSpirit.value = spirit
    }

    fun onMatchFilterSelect(filter: MatchFilter) {
        _selectedMatchFilter.value = filter
    }

    fun toggleIngredientStock(ingredientId: Int, inStock: Boolean) {
        viewModelScope.launch {
            repository.toggleIngredientStock(ingredientId, inStock)
        }
    }

    fun setAllStockForVisible(inStock: Boolean) {
        viewModelScope.launch {
            filteredIngredients.value.forEach { ing ->
                repository.toggleIngredientStock(ing.id, inStock)
            }
        }
    }

    fun clearAllStock() {
        viewModelScope.launch {
            repository.setAllIngredientsStock(false)
        }
    }

    fun toggleFavorite(recipeId: Int, currentIsFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(recipeId, !currentIsFavorite)
        }
    }

    fun deleteCustomRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.deleteCustomRecipe(recipeId)
        }
    }

    fun updateCustomRecipe(
        recipeId: Int,
        name: String,
        description: String,
        glassType: String,
        ice: String,
        garnish: String,
        instructions: String,
        baseSpirit: String,
        isFavorite: Boolean,
        ingredients: List<CustomIngredientInput>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.updateCustomRecipe(
                recipeId = recipeId,
                name = name,
                description = description,
                glassType = glassType,
                ice = ice,
                garnish = garnish,
                instructions = instructions,
                baseSpirit = baseSpirit,
                isFavorite = isFavorite,
                ingredientsInput = ingredients
            )
            onSuccess()
        }
    }

    fun createCustomRecipe(
        name: String,
        description: String,
        glassType: String,
        ice: String,
        garnish: String,
        instructions: String,
        baseSpirit: String,
        ingredients: List<CustomIngredientInput>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createCustomRecipe(
                name = name,
                description = description,
                glassType = glassType,
                ice = ice,
                garnish = garnish,
                instructions = instructions,
                baseSpirit = baseSpirit,
                ingredientsInput = ingredients
            )
            onSuccess()
        }
    }

    fun exportCustomBackupJson(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportCustomBackupJson()
            onResult(json)
        }
    }

    fun exportAllBackupJson(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportAllBackupJson()
            onResult(json)
        }
    }

    fun importBackupJson(jsonString: String, onResult: (com.example.util.ImportResult) -> Unit) {
        viewModelScope.launch {
            val result = repository.importBackupJson(jsonString)
            onResult(result)
        }
    }
}

class CocktailViewModelFactory(private val repository: CocktailRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CocktailViewModel::class.java)) {
            return CocktailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
