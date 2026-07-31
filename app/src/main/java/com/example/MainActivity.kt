package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.model.RecipeWithMatch
import com.example.data.repository.CocktailRepository
import com.example.ui.CocktailViewModel
import com.example.ui.CocktailViewModelFactory
import com.example.ui.RecipeTab
import com.example.ui.dialogs.BackupDialog
import com.example.ui.dialogs.CreateRecipeDialog
import com.example.ui.dialogs.ShareRecipeDialog
import com.example.ui.dialogs.TipJarDialog
import com.example.ui.screens.MyBarScreen
import com.example.ui.screens.RecipeDetailScreen
import com.example.ui.screens.RecipesScreen
import com.example.ui.theme.CraftCocktailTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CraftCocktailTheme {
                CocktailApp()
            }
        }
    }
}

@Composable
fun CocktailApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { CocktailRepository(database.cocktailDao()) }
    val viewModel: CocktailViewModel = viewModel(factory = CocktailViewModelFactory(repository))

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Dialog state controllers
    var showCreateRecipeDialog by remember { mutableStateOf(false) }
    var editingRecipeMatch by remember { mutableStateOf<RecipeWithMatch?>(null) }
    var shareRecipeMatch by remember { mutableStateOf<RecipeWithMatch?>(null) }
    var showTipJarDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Only show bottom navigation on main tabs
            if (currentRoute == "my_bar" || currentRoute == "recipes") {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "my_bar",
                        onClick = {
                            navController.navigate("my_bar") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == "my_bar") Icons.Default.LocalBar else Icons.Outlined.LocalBar,
                                contentDescription = "My Bar"
                            )
                        },
                        label = { Text("My Bar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_my_bar")
                    )

                    NavigationBarItem(
                        selected = currentRoute == "recipes",
                        onClick = {
                            navController.navigate("recipes") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == "recipes") Icons.Default.MenuBook else Icons.Outlined.MenuBook,
                                contentDescription = "Recipes"
                            )
                        },
                        label = { Text("Recipes", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_recipes")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "my_bar",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("my_bar") {
                MyBarScreen(
                    viewModel = viewModel,
                    onNavigateToRecommendations = {
                        viewModel.onRecipeTabSelect(RecipeTab.RECOMMENDED)
                        navController.navigate("recipes") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenBackup = {
                        showBackupDialog = true
                    }
                )
            }

            composable("recipes") {
                RecipesScreen(
                    viewModel = viewModel,
                    onSelectRecipe = { recipeId ->
                        navController.navigate("recipe_detail/$recipeId")
                    },
                    onOpenCreateRecipe = {
                        showCreateRecipeDialog = true
                    },
                    onOpenTipJar = {
                        showTipJarDialog = true
                    },
                    onOpenBackup = {
                        showBackupDialog = true
                    },
                    onEditRecipe = { match ->
                        editingRecipeMatch = match
                    }
                )
            }

            composable(
                route = "recipe_detail/{recipeId}",
                arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
                RecipeDetailScreen(
                    recipeId = recipeId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenShare = { match -> shareRecipeMatch = match },
                    onOpenEditRecipe = { match -> editingRecipeMatch = match }
                )
            }
        }

        // --- Dialogs & Modals ---
        if (showCreateRecipeDialog || editingRecipeMatch != null) {
            CreateRecipeDialog(
                viewModel = viewModel,
                recipeToEdit = editingRecipeMatch,
                onDismiss = {
                    showCreateRecipeDialog = false
                    editingRecipeMatch = null
                }
            )
        }

        shareRecipeMatch?.let { match ->
            ShareRecipeDialog(
                recipeMatch = match,
                onDismiss = { shareRecipeMatch = null }
            )
        }

        if (showTipJarDialog) {
            TipJarDialog(
                onDismiss = { showTipJarDialog = false }
            )
        }

        if (showBackupDialog) {
            BackupDialog(
                viewModel = viewModel,
                onDismiss = { showBackupDialog = false }
            )
        }
    }
}
