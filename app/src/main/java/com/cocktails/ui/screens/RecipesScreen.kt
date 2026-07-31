package com.cocktails.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocktails.data.model.MatchFilter
import com.cocktails.data.model.RecipeWithMatch
import com.cocktails.ui.CocktailViewModel
import com.cocktails.ui.RecipeTab
import com.cocktails.ui.theme.MissingAmber
import com.cocktails.ui.theme.MissingAmberLight
import com.cocktails.ui.theme.StockGreen
import com.cocktails.ui.theme.StockGreenLight
import com.cocktails.util.RecipeScaler

val BASE_SPIRIT_OPTIONS = listOf("All", "Bourbon", "Tequila", "Gin", "Vodka", "Rum", "Prosecco")

@Composable
fun RecipesScreen(
    viewModel: CocktailViewModel,
    onSelectRecipe: (Int) -> Unit,
    onOpenCreateRecipe: () -> Unit,
    onOpenTipJar: () -> Unit,
    onOpenBackup: () -> Unit = {},
    onEditRecipe: ((RecipeWithMatch) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.recipeSearchQuery.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedRecipeTab.collectAsStateWithLifecycle()
    val selectedBaseSpirit by viewModel.selectedBaseSpirit.collectAsStateWithLifecycle()
    val selectedMatchFilter by viewModel.selectedMatchFilter.collectAsStateWithLifecycle()
    val filteredRecipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()

    var recipeToDelete by remember { mutableStateOf<RecipeWithMatch?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- Header & Tip Jar Action ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Craft Cocktails",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Filter recipes based on your home bar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Google Drive Backup / Export & Import Button
                    IconButton(
                        onClick = onOpenBackup,
                        modifier = Modifier.testTag("backup_drive_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Google Drive Backup & Import",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Tip Jar Button ("Buy dev a cocktail")
                    IconButton(
                        onClick = onOpenTipJar,
                        modifier = Modifier.testTag("tip_jar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = "Buy Developer a Cocktail",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // --- Recipe Tabs ---
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                RecipeTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { viewModel.onRecipeTabSelect(tab) },
                        text = { Text(tab.label, fontSize = 12.sp, fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium) },
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }

            // --- Search Field ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onRecipeSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("recipe_search_input"),
                placeholder = { Text("Search drinks or ingredients (e.g. Margarita)...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onRecipeSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // --- Base Spirit Filter Chips ---
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 2.dp)
            ) {
                items(BASE_SPIRIT_OPTIONS) { spirit ->
                    val isSelected = selectedBaseSpirit == spirit
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onBaseSpiritSelect(spirit) },
                        label = { Text(text = spirit, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            // --- Match Filter Chips ---
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 2.dp)
            ) {
                items(MatchFilter.entries.toTypedArray()) { matchFilter ->
                    val isSelected = selectedMatchFilter == matchFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onMatchFilterSelect(matchFilter) },
                        label = { Text(text = matchFilter.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- Recipes List ---
            if (filteredRecipes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.WineBar,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching cocktails",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Try adding more ingredients to your bar or clearing search filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = filteredRecipes,
                        key = { it.recipe.id }
                    ) { item ->
                        CocktailCard(
                            item = item,
                            onClick = { onSelectRecipe(item.recipe.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(item.recipe.id, item.recipe.isFavorite) },
                            onEditRecipe = if (item.recipe.isCustom) onEditRecipe else null,
                            onDeleteRecipe = { recipeToDelete = it }
                        )
                    }
                }
            }
        }

        // --- Delete Confirmation Dialog ---
        recipeToDelete?.let { item ->
            AlertDialog(
                onDismissRequest = { recipeToDelete = null },
                title = {
                    Text(text = "Delete Custom Recipe?", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("Are you sure you want to delete \"${item.recipe.name}\"? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteCustomRecipe(item.recipe.id)
                            recipeToDelete = null
                        },
                        modifier = Modifier.testTag("confirm_delete_card_recipe_button")
                    ) {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { recipeToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // --- FAB to Create Custom Recipe ---
        FloatingActionButton(
            onClick = onOpenCreateRecipe,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_custom_recipe_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Create Custom Recipe")
        }
    }
}

@Composable
fun CocktailCard(
    item: RecipeWithMatch,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEditRecipe: ((RecipeWithMatch) -> Unit)? = null,
    onDeleteRecipe: ((RecipeWithMatch) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val recipe = item.recipe
    var isExpanded by remember { mutableStateOf(false) }
    var cardScaleFactor by remember { mutableFloatStateOf(1.0f) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (item.isFullyMatch) StockGreen.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .testTag("recipe_card_${recipe.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val nameFontSize = when {
                        recipe.name.length > 28 -> 13.sp
                        recipe.name.length > 20 -> 14.sp
                        recipe.name.length > 15 -> 15.sp
                        else -> 16.sp
                    }
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = nameFontSize,
                            lineHeight = (nameFontSize.value + 4).sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = recipe.baseSpirit,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = recipe.glassType,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (recipe.isCustom) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Custom",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (recipe.isCustom && onEditRecipe != null) {
                        IconButton(
                            onClick = { onEditRecipe(item) },
                            modifier = Modifier.testTag("edit_recipe_${recipe.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Recipe",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (recipe.isCustom && onDeleteRecipe != null) {
                        IconButton(
                            onClick = { onDeleteRecipe(item) },
                            modifier = Modifier.testTag("delete_recipe_${recipe.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Recipe",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.testTag("favorite_recipe_${recipe.id}")
                    ) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (recipe.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // --- Match Pill Indicator & Scale Button ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (item.isFullyMatch) {
                    Box(
                        modifier = Modifier
                            .background(StockGreenLight, RoundedCornerShape(8.dp))
                            .border(1.dp, StockGreen, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = StockGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ready to Make! (${item.inStockIngredientsCount}/${item.requiredIngredientsCount})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = StockGreen
                            )
                        }
                    }
                } else if (item.missingIngredientNames.size == 1) {
                    Box(
                        modifier = Modifier
                            .background(MissingAmberLight, RoundedCornerShape(8.dp))
                            .border(1.dp, MissingAmber, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Missing 1: ${item.missingIngredientNames.first()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MissingAmber
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${item.inStockIngredientsCount}/${item.requiredIngredientsCount} Ingredients in Bar",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Expandable Recipe Scaling Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isExpanded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                        .clickable { isExpanded = !isExpanded }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("expand_scale_card_${recipe.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val badgeLabel = if (cardScaleFactor % 1f == 0f) "${cardScaleFactor.toInt()}x" else "${cardScaleFactor}x"
                        Text(
                            text = if (cardScaleFactor != 1.0f) "Scale ($badgeLabel)" else "Scale",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isExpanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle recipe scaling",
                            tint = if (isExpanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Batch Size:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf(0.5f, 1f, 2f, 3f, 4f).forEach { scale ->
                                        val isSelected = cardScaleFactor == scale
                                        val label = if (scale % 1f == 0f) "${scale.toInt()}x" else "${scale}x"
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                                )
                                                .clickable { cardScaleFactor = scale }
                                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            item.ingredients.forEach { ing ->
                                val scaledAmt = RecipeScaler.scaleAmount(ing.amount, cardScaleFactor)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ing.ingredientName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = scaledAmt,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
