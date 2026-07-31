# Craft Cocktail 🍸

An intuitive, modern Android app built with **Jetpack Compose**, **Kotlin Coroutines**, and **Room Database** that helps you discover what cocktails you can make with the ingredients you already have in your home bar!

---

## ✨ Features

- 🍹 **My Bar Inventory Manager**: Easily toggle ingredients in and out of stock. Categorized by Spirits, Liqueurs, Mixers, Bitters, Juices, Fresh Produce, and Garnishes.
- 🎯 **Smart Recipe Matching**: Instant feedback on cocktails you can make right now (**100% Match**) vs. drinks missing 1 or 2 ingredients (**Missing Spirits/Mixers**).
- ⚖️ **Dynamic Recipe Scaling (0.5x – 10x Batching)**: Easily scale cocktail ingredient quantities up or down. Whether you're making a single drink or batching for a party of 6, ingredient fractions (e.g. `3/4 oz`, `1 1/2 oz`) update automatically with adjusted unit plurals (`dashes`, `splashes`, `oz`).
- ✍️ **Custom Cocktail Creator & Editor**: Add your own custom cocktail recipes or edit custom creations with custom ingredients, glass types, ice preferences, and garnishes.
- ☁️ **Google Drive & JSON Backup & Restore**: Export custom recipes and bar inventory directly to Google Drive or local storage as standard JSON format, and import backups anytime.
- 📱 **Share & Export Card**: Share beautiful cocktail recipe summary cards with friends via messaging apps, email, or social media.
- ☕ **Tip Jar / Support Dev**: Integrated developer tip jar to buy the creator a virtual cocktail.

---

## 🛠 Tech Stack & Architecture

- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture repository pattern
- **Database**: Room Database with KSP
- **Async & Reactive Flow**: Kotlin Coroutines & `StateFlow` / `collectAsStateWithLifecycle`
- **Navigation**: Jetpack Navigation Compose
- **JSON Engine**: Native Android `org.json`
- **System Integration**: Storage Access Framework (SAF) & Android System Share Sheet for Google Drive export/import

---

## 📁 Project Structure

```
app/src/main/java/com/example/
├── data/
│   ├── local/               # Room Entities (Ingredient, Recipe, RecipeIngredient) & DAO
│   ├── model/               # Domain Models & Data Transfer Objects (RecipeWithMatch)
│   └── repository/          # CocktailRepository (Data orchestration & JSON Backup)
├── ui/
│   ├── dialogs/             # Create/Edit Recipe, Google Drive Backup, Share, Tip Jar Dialogs
│   ├── screens/             # MyBarScreen, RecipesScreen, RecipeDetailScreen
│   ├── theme/               # Material Design 3 Palette & Typography
│   ├── CocktailViewModel.kt # Central State Management & ViewModel
│   └── Navigation.kt        # Navigation Routes & Composables
├── util/
│   ├── BackupJsonManager.kt # JSON Exporter/Importer parser
│   └── RecipeScaler.kt     # Smart Fraction & Multiplier Scaler Engine
└── MainActivity.kt          # Main Activity & Navigation Host
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug (or newer)
- **JDK**: 17+
- **Min SDK**: 24 (Android 7.0+)
- **Target SDK**: 35 (Android 15)

### Building & Running

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/craft-cocktail.git
   cd craft-cocktail
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync dependencies.
4. Select an Android Emulator or connected physical device.
5. Click **Run (`Shift + F10`)**.

---

## 🧪 Testing

Run JVM unit tests:
```bash
./gradlew testDebugUnitTest
```

---

## 💸 Contribute to the developer

Craft Cocktail is open and free to use! If this app helped you mix a great drink, consider supporting future features and updates.

PayPal link: https://www.paypal.com/donate/?business=U265W7M8EPXTJ&no_recurring=0&item_name=Help+fund+the+Craft+Cocktails+app&currency_code=USD


## 📄 License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
