package com.mjp5153.craft.cocktails.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mjp5153.craft.cocktails.data.model.RecipeWithMatch
import com.mjp5153.craft.cocktails.data.repository.CustomIngredientInput
import com.mjp5153.craft.cocktails.ui.CocktailViewModel

data class IngredientRowState(
    var name: String = "",
    var amount: String = "1 oz",
    var category: String = "Spirits",
    var isOptional: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeDialog(
    viewModel: CocktailViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    recipeToEdit: RecipeWithMatch? = null
) {
    val isEditing = recipeToEdit != null
    val existingRecipe = recipeToEdit?.recipe

    var name by remember { mutableStateOf(existingRecipe?.name ?: "") }
    var description by remember { mutableStateOf(existingRecipe?.description ?: "") }
    var baseSpirit by remember { mutableStateOf(existingRecipe?.baseSpirit ?: "Bourbon") }
    var glassType by remember { mutableStateOf(existingRecipe?.glassType ?: "Rocks Glass") }
    var iceType by remember { mutableStateOf(existingRecipe?.ice ?: "Cubed Ice") }
    var garnish by remember { mutableStateOf(existingRecipe?.garnish ?: "Orange Twist") }
    var instructions by remember { mutableStateOf(existingRecipe?.instructions ?: "") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var instructionsError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val ingredientRows = remember {
        if (recipeToEdit != null && recipeToEdit.ingredients.isNotEmpty()) {
            mutableStateListOf<IngredientRowState>().apply {
                addAll(
                    recipeToEdit.ingredients.map { ing ->
                        IngredientRowState(
                            name = ing.ingredientName,
                            amount = ing.amount,
                            category = "Spirits",
                            isOptional = ing.isOptional
                        )
                    }
                )
            }
        } else {
            mutableStateListOf(
                IngredientRowState("Bourbon", "2 oz", "Spirits"),
                IngredientRowState("Angostura Bitters", "2 dashes", "Bitters & Syrups")
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .consumeWindowInsets(WindowInsets.safeDrawing)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "Edit Custom Recipe" else "Create Custom Recipe",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = null
                        },
                        label = { Text("Cocktail Name *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_recipe_name"),
                        singleLine = true,
                        isError = nameError != null,
                        supportingText = nameError?.let { { Text(it) } }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Short Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_recipe_desc"),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = baseSpirit,
                            onValueChange = { baseSpirit = it },
                            label = { Text("Base Spirit") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_base_spirit"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = glassType,
                            onValueChange = { glassType = it },
                            label = { Text("Glassware") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_glass_type"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = iceType,
                            onValueChange = { iceType = it },
                            label = { Text("Ice") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_ice_type"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = garnish,
                            onValueChange = { garnish = it },
                            label = { Text("Garnish") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_garnish"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Any new ingredients will be automatically saved to your inventory database.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ingredientRows.forEachIndexed { idx, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = row.name,
                                onValueChange = { ingredientRows[idx] = row.copy(name = it) },
                                placeholder = { Text("Ingredient name") },
                                modifier = Modifier
                                    .weight(2f)
                                    .testTag("input_ing_name_$idx"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = row.amount,
                                onValueChange = { ingredientRows[idx] = row.copy(amount = it) },
                                placeholder = { Text("2 oz") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_ing_amount_$idx"),
                                singleLine = true
                            )

                            IconButton(
                                onClick = {
                                    if (ingredientRows.size > 1) {
                                        ingredientRows.removeAt(idx)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove ingredient",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            ingredientRows.add(IngredientRowState())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("add_ingredient_row_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Ingredient")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = instructions,
                        onValueChange = {
                            instructions = it
                            instructionsError = null
                        },
                        label = { Text("Step-by-step Instructions *") },
                        placeholder = { Text("1. Combine in shaker with ice.\n2. Shake hard for 15s.\n3. Strain into glass.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .imePadding()
                            .testTag("input_recipe_instructions"),
                        isError = instructionsError != null,
                        supportingText = instructionsError?.let { { Text(it) } }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (isEditing && recipeToEdit != null) {
                        TextButton(
                            onClick = { showDeleteConfirm = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.testTag("delete_recipe_button_dialog")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Recipe",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                var hasError = false
                                if (name.isBlank()) {
                                    nameError = "Please enter a cocktail name."
                                    hasError = true
                                }
                                if (instructions.isBlank()) {
                                    instructionsError = "Please enter step-by-step instructions."
                                    hasError = true
                                }

                                val inputs = ingredientRows
                                    .filter { it.name.isNotBlank() }
                                    .map {
                                        CustomIngredientInput(
                                            name = it.name,
                                            amount = it.amount,
                                            category = it.category,
                                            isOptional = it.isOptional
                                        )
                                    }

                                if (inputs.isEmpty()) {
                                    errorMessage = "Please add at least one ingredient."
                                    hasError = true
                                } else {
                                    errorMessage = null
                                }

                                if (hasError) return@Button

                                if (isEditing && recipeToEdit != null) {
                                    viewModel.updateCustomRecipe(
                                        recipeId = recipeToEdit.recipe.id,
                                        name = name,
                                        description = description,
                                        glassType = glassType,
                                        ice = iceType,
                                        garnish = garnish,
                                        instructions = instructions,
                                        baseSpirit = baseSpirit,
                                        isFavorite = recipeToEdit.recipe.isFavorite,
                                        ingredients = inputs,
                                        onSuccess = onDismiss
                                    )
                                } else {
                                    viewModel.createCustomRecipe(
                                        name = name,
                                        description = description,
                                        glassType = glassType,
                                        ice = iceType,
                                        garnish = garnish,
                                        instructions = instructions,
                                        baseSpirit = baseSpirit,
                                        ingredients = inputs,
                                        onSuccess = onDismiss
                                    )
                                }
                            },
                            modifier = Modifier.testTag("save_recipe_button")
                        ) {
                            Text("Save Recipe")
                        }
                    }
                }

                if (showDeleteConfirm && recipeToEdit != null) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirm = false },
                        title = {
                            Text(text = "Delete Custom Recipe?", fontWeight = FontWeight.Bold)
                        },
                        text = {
                            Text("Are you sure you want to delete \"${recipeToEdit.recipe.name}\"? This action cannot be undone.")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDeleteConfirm = false
                                    viewModel.deleteCustomRecipe(recipeToEdit.recipe.id)
                                    onDismiss()
                                },
                                modifier = Modifier.testTag("confirm_dialog_delete_button")
                            ) {
                                Text(
                                    text = "Delete",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteConfirm = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}
