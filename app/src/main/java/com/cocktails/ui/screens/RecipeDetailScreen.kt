package com.cocktails.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocktails.data.model.RecipeWithMatch
import com.cocktails.ui.CocktailViewModel
import com.cocktails.ui.theme.MissingAmber
import com.cocktails.ui.theme.MissingAmberLight
import com.cocktails.ui.theme.StockGreen
import com.cocktails.ui.theme.StockGreenLight
import com.cocktails.util.RecipeScaler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    viewModel: CocktailViewModel,
    onBack: () -> Unit,
    onOpenShare: (RecipeWithMatch) -> Unit,
    onOpenEditRecipe: (RecipeWithMatch) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val allRecipes by viewModel.allRecipesWithMatch.collectAsStateWithLifecycle()
    val allIngredients by viewModel.allIngredients.collectAsStateWithLifecycle()

    val recipeMatch = allRecipes.find { it.recipe.id == recipeId }

    if (recipeMatch == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Recipe not found.")
        }
        return
    }

    val recipe = recipeMatch.recipe
    val inStockSet = remember(allIngredients) {
        allIngredients.filter { it.inStock }.map { it.name.lowercase().trim() }.toSet()
    }

    // Recipe scaling multiplier (1x = default 1 drink)
    var scaleFactor by remember { mutableFloatStateOf(1.0f) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Step completion tracking state
    val stepCheckedState = remember { mutableStateMapOf<Int, Boolean>() }
    val instructionSteps = remember(recipe.instructions) {
        recipe.instructions
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleFontSize = when {
                        recipe.name.length > 25 -> 15.sp
                        recipe.name.length > 18 -> 17.sp
                        else -> 18.sp
                    }
                    Text(
                        text = recipe.name,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = titleFontSize),
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("recipe_detail_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleFavorite(recipe.id, recipe.isFavorite) },
                        modifier = Modifier.testTag("recipe_detail_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (recipe.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { onOpenShare(recipeMatch) },
                        modifier = Modifier.testTag("recipe_detail_share_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }
                    if (recipe.isCustom) {
                        IconButton(
                            onClick = { onOpenEditRecipe(recipeMatch) },
                            modifier = Modifier.testTag("recipe_detail_edit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Recipe"
                            )
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.testTag("recipe_detail_delete_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Recipe",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(
                            text = "Delete Custom Recipe?",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text("Are you sure you want to delete \"${recipe.name}\"? This action cannot be undone.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deleteCustomRecipe(recipe.id)
                                onBack()
                            },
                            modifier = Modifier.testTag("confirm_delete_recipe_button")
                        ) {
                            Text(
                                text = "Delete",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false },
                            modifier = Modifier.testTag("cancel_delete_recipe_button")
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // --- Hero Metadata Tags ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = recipe.baseSpirit,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = recipe.glassType,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        if (recipe.ice.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = recipe.ice,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (recipe.garnish.isNotBlank() && recipe.garnish != "None") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Garnish: ${recipe.garnish}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Match Inventory Status Banner ---
            if (recipeMatch.isFullyMatch) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StockGreenLight, RoundedCornerShape(12.dp))
                        .border(1.dp, StockGreen, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = StockGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "You have all required ingredients in your bar!",
                            fontWeight = FontWeight.Bold,
                            color = StockGreen,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MissingAmberLight, RoundedCornerShape(12.dp))
                        .border(1.dp, MissingAmber, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MissingAmber)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Missing ${recipeMatch.missingIngredientNames.size} required ingredient(s): ${recipeMatch.missingIngredientNames.joinToString()}",
                            fontWeight = FontWeight.Medium,
                            color = MissingAmber,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Ingredients Section Header with Scale Controls ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Stepper control: [-] [ multiplier ] [+]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                if (scaleFactor > 0.5f) {
                                    scaleFactor = (scaleFactor - 0.5f).coerceAtLeast(0.5f)
                                }
                            }
                            .testTag("recipe_scale_decrease"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease portion",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        val scaleLabel = if (scaleFactor % 1f == 0f) "${scaleFactor.toInt()}x" else "${scaleFactor}x"
                        Text(
                            text = scaleLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                if (scaleFactor < 10.0f) {
                                    scaleFactor += 0.5f
                                }
                            }
                            .testTag("recipe_scale_increase"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase portion",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Batch Scale Preset Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Batch:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                listOf(0.5f, 1f, 2f, 3f, 4f, 6f).forEach { scalePreset ->
                    val isSelected = scaleFactor == scalePreset
                    val label = if (scalePreset % 1f == 0f) "${scalePreset.toInt()}x" else "${scalePreset}x"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { scaleFactor = scalePreset }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("scale_chip_${label}")
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (scaleFactor != 1.0f) {
                        val servingsText = when (scaleFactor) {
                            0.5f -> "Half batch (0.5x)"
                            1.0f -> "1 drink"
                            else -> if (scaleFactor % 1f == 0f) "${scaleFactor.toInt()} drinks (${scaleFactor.toInt()}x batch)" else "${scaleFactor}x batch"
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.WineBar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Amounts scaled for $servingsText",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    recipeMatch.ingredients.forEachIndexed { index, ing ->
                        val hasInStock = inStockSet.contains(ing.ingredientName.lowercase().trim())
                        val scaledAmount = RecipeScaler.scaleAmount(ing.amount, scaleFactor)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(
                                            color = if (hasInStock) StockGreenLight else MissingAmberLight,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (hasInStock) Icons.Default.Check else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (hasInStock) StockGreen else MissingAmber,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = ing.ingredientName,
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (ing.isOptional) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(Optional)",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = scaledAmount,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (index < recipeMatch.ingredients.lastIndex) {
                            Divider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Step-by-Step Instructions ---
            Text(
                text = "Preparation Steps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    instructionSteps.forEachIndexed { stepIdx, stepText ->
                        val isChecked = stepCheckedState[stepIdx] ?: false

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { stepCheckedState[stepIdx] = !isChecked }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { stepCheckedState[stepIdx] = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stepText,
                                style = MaterialTheme.typography.bodyMedium,
                                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        if (stepIdx < instructionSteps.lastIndex) {
                            Divider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }
    }
}
