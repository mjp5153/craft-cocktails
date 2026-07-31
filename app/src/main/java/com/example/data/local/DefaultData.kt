package com.example.data.local

/**
 * Data structure for default pre-populated recipes.
 */
data class InitialRecipe(
    val name: String,
    val description: String,
    val glassType: String,
    val ice: String,
    val garnish: String,
    val instructions: String,
    val baseSpirit: String,
    val ingredients: List<InitialIngredient>
)

data class InitialIngredient(
    val name: String,
    val amount: String,
    val category: String,
    val isOptional: Boolean = false
)

object DefaultData {

    val PREPOPULATED_RECIPES = listOf(
        InitialRecipe(
            name = "Old Fashioned",
            description = "A timeless, spirit-forward classic combining whiskey, bitters, and sugar for a rich, aromatic drink.",
            glassType = "Rocks Glass",
            ice = "Large Ice Cube",
            garnish = "Orange Peel & Amarena Cherry",
            instructions = "1. Muddle the sugar cube/syrup and bitters with a splash of water in a rocks glass.\n2. Add a large ice cube and pour in the Bourbon or Rye Whiskey.\n3. Stir gently for 20-30 seconds until chilled.\n4. Express oil from the orange peel over the glass and drop it in.",
            baseSpirit = "Bourbon",
            ingredients = listOf(
                InitialIngredient("Bourbon", "2 oz", "Spirits"),
                InitialIngredient("Angostura Bitters", "3 dashes", "Bitters & Syrups"),
                InitialIngredient("Simple Syrup", "1/4 oz", "Bitters & Syrups"),
                InitialIngredient("Water", "1 splash", "Mixers & Juices", isOptional = true),
                InitialIngredient("Orange Peel", "1 twist", "Garnishes & Ice")
            )
        ),
        InitialRecipe(
            name = "Margarita",
            description = "The classic Mexican cocktail balancing crisp tequila, tart lime juice, and sweet orange liqueur.",
            glassType = "Rocks Glass / Coupe",
            ice = "Cubed Ice",
            garnish = "Lime Wheel & Salt Rim",
            instructions = "1. Run a lime wedge around the rim of your glass and dip into coarse salt.\n2. In a shaker filled with ice, combine Tequila, Fresh Lime Juice, and Cointreau.\n3. Shake vigorously for 15 seconds until frosty.\n4. Strain over fresh ice into the prepared glass.",
            baseSpirit = "Tequila",
            ingredients = listOf(
                InitialIngredient("Tequila", "2 oz", "Spirits"),
                InitialIngredient("Fresh Lime Juice", "1 oz", "Citrus & Produce"),
                InitialIngredient("Cointreau", "3/4 oz", "Liqueurs & Fortified"),
                InitialIngredient("Agave Nectar", "1/4 oz", "Bitters & Syrups", isOptional = true),
                InitialIngredient("Salt", "For rim", "Garnishes & Ice", isOptional = true),
                InitialIngredient("Lime Wheel", "1 wheel", "Garnishes & Ice")
            )
        ),
        InitialRecipe(
            name = "Martini",
            description = "The sophisticated icon of cocktail culture, perfectly chilled with botanical gin and dry vermouth.",
            glassType = "Martini Glass / Coupe",
            ice = "None (Chilled Glass)",
            garnish = "Lemon Twist or Olives",
            instructions = "1. Pre-chill martini glass in freezer.\n2. Combine Gin and Dry Vermouth in a mixing glass filled with ice.\n3. Stir continuously for 30 seconds until thoroughly chilled.\n4. Strain into the chilled martini glass and garnish with a lemon twist or olive.",
            baseSpirit = "Gin",
            ingredients = listOf(
                InitialIngredient("Gin", "2.5 oz", "Spirits"),
                InitialIngredient("Dry Vermouth", "1/2 oz", "Liqueurs & Fortified"),
                InitialIngredient("Orange Bitters", "1 dash", "Bitters & Syrups", isOptional = true),
                InitialIngredient("Olive", "1 or 3", "Garnishes & Ice"),
                InitialIngredient("Lemon Twist", "1 twist", "Garnishes & Ice", isOptional = true)
            )
        ),
        InitialRecipe(
            name = "Moscow Mule",
            description = "A refreshing kick of spicy ginger beer, crisp vodka, and zesty fresh lime juice served ice cold.",
            glassType = "Copper Mug",
            ice = "Crushed Ice",
            garnish = "Lime Wheel & Mint Sprig",
            instructions = "1. Fill a copper mug with crushed ice.\n2. Add Vodka and Fresh Lime Juice.\n3. Top with cold Ginger Beer and stir gently to combine.\n4. Garnish with a fresh lime wheel and mint sprig.",
            baseSpirit = "Vodka",
            ingredients = listOf(
                InitialIngredient("Vodka", "2 oz", "Spirits"),
                InitialIngredient("Fresh Lime Juice", "1/2 oz", "Citrus & Produce"),
                InitialIngredient("Ginger Beer", "4 oz", "Mixers & Juices"),
                InitialIngredient("Lime Wheel", "1 wheel", "Garnishes & Ice")
            )
        ),
        InitialRecipe(
            name = "Daiquiri",
            description = "A pristine Cuban classic uniting white rum, freshly squeezed lime, and simple syrup in harmony.",
            glassType = "Coupe Glass",
            ice = "None (Chilled)",
            garnish = "Lime Wheel",
            instructions = "1. Combine White Rum, Fresh Lime Juice, and Simple Syrup in a cocktail shaker.\n2. Fill shaker with ice and shake vigorously for 15 seconds.\n3. Fine-strain into a chilled coupe glass.\n4. Float a lime wheel on top.",
            baseSpirit = "Rum",
            ingredients = listOf(
                InitialIngredient("White Rum", "2 oz", "Spirits"),
                InitialIngredient("Fresh Lime Juice", "1 oz", "Citrus & Produce"),
                InitialIngredient("Simple Syrup", "3/4 oz", "Bitters & Syrups"),
                InitialIngredient("Lime Wheel", "1 wheel", "Garnishes & Ice", isOptional = true)
            )
        ),
        InitialRecipe(
            name = "Negroni",
            description = "An Italian staple offering an equal-parts harmony of bitter Campari, sweet vermouth, and aromatic gin.",
            glassType = "Rocks Glass",
            ice = "Large Ice Cube",
            garnish = "Orange Peel",
            instructions = "1. Combine Gin, Campari, and Sweet Vermouth in a rocks glass filled with ice.\n2. Stir well for 20-25 seconds until cold.\n3. Garnish with an orange peel after expressing its citrus oils over the drink.",
            baseSpirit = "Gin",
            ingredients = listOf(
                InitialIngredient("Gin", "1 oz", "Spirits"),
                InitialIngredient("Campari", "1 oz", "Liqueurs & Fortified"),
                InitialIngredient("Sweet Vermouth", "1 oz", "Liqueurs & Fortified"),
                InitialIngredient("Orange Peel", "1 twist", "Garnishes & Ice")
            )
        ),
        InitialRecipe(
            name = "Espresso Martini",
            description = "A decadent, energetic cocktail blending rich fresh espresso, vodka, and rich coffee liqueur.",
            glassType = "Coupe / Martini Glass",
            ice = "None (Chilled)",
            garnish = "3 Coffee Beans",
            instructions = "1. Brew fresh espresso and allow it to cool slightly.\n2. Add Vodka, Coffee Liqueur, Fresh Espresso, and Simple Syrup into a shaker filled with ice.\n3. Shake hard for 20 seconds to create a thick, velvety foam head.\n4. Strain quickly into a coupe glass and top with 3 coffee beans.",
            baseSpirit = "Vodka",
            ingredients = listOf(
                InitialIngredient("Vodka", "1.5 oz", "Spirits"),
                InitialIngredient("Coffee Liqueur", "3/4 oz", "Liqueurs & Fortified"),
                InitialIngredient("Fresh Espresso", "1 oz", "Mixers & Juices"),
                InitialIngredient("Simple Syrup", "1/4 oz", "Bitters & Syrups"),
                InitialIngredient("Coffee Beans", "3 beans", "Garnishes & Ice", isOptional = true)
            )
        ),
        InitialRecipe(
            name = "Whiskey Sour",
            description = "A silky blend of bourbon, tart lemon, and sweet syrup, elevated with an optional velvety egg white foam.",
            glassType = "Rocks Glass",
            ice = "Cubed Ice",
            garnish = "Angostura Bitters Drops & Cherry",
            instructions = "1. Add Bourbon, Fresh Lemon Juice, Simple Syrup, and Egg White into a shaker without ice and dry shake for 15 seconds.\n2. Add ice and wet shake hard for another 15 seconds.\n3. Strain into a rocks glass over ice.\n4. Top foam with 3 drops of Angostura bitters.",
            baseSpirit = "Bourbon",
            ingredients = listOf(
                InitialIngredient("Bourbon", "2 oz", "Spirits"),
                InitialIngredient("Fresh Lemon Juice", "3/4 oz", "Citrus & Produce"),
                InitialIngredient("Simple Syrup", "3/4 oz", "Bitters & Syrups"),
                InitialIngredient("Egg White", "1 white", "Other", isOptional = true),
                InitialIngredient("Angostura Bitters", "2 drops", "Bitters & Syrups", isOptional = true)
            )
        ),
        InitialRecipe(
            name = "Mojito",
            description = "A vibrant Cuban cooler packed with muddled mint leaves, rum, tart lime, and fizzy soda.",
            glassType = "Highball Glass",
            ice = "Crushed Ice",
            garnish = "Mint Sprig & Lime Wheel",
            instructions = "1. Gently muddle mint leaves with Simple Syrup and Fresh Lime Juice in the bottom of a highball glass.\n2. Add White Rum and fill 3/4 with crushed ice.\n3. Stir with a bar spoon to draw mint through the drink.\n4. Top with Club Soda and garnish with a bushy mint sprig.",
            baseSpirit = "Rum",
            ingredients = listOf(
                InitialIngredient("White Rum", "2 oz", "Spirits"),
                InitialIngredient("Fresh Lime Juice", "3/4 oz", "Citrus & Produce"),
                InitialIngredient("Simple Syrup", "1/2 oz", "Bitters & Syrups"),
                InitialIngredient("Mint Leaves", "8-10 leaves", "Citrus & Produce"),
                InitialIngredient("Club Soda", "2 oz", "Mixers & Juices"),
                InitialIngredient("Lime Wheel", "1 wheel", "Garnishes & Ice", isOptional = true)
            )
        ),
        InitialRecipe(
            name = "Cosmopolitan",
            description = "A bright pink 90s glam staple pairing citrus vodka, orange liqueur, fresh lime, and cranberry.",
            glassType = "Coupe Glass",
            ice = "None (Chilled)",
            garnish = "Orange Twist",
            instructions = "1. Combine Vodka, Cointreau, Fresh Lime Juice, and Cranberry Juice in a shaker filled with ice.\n2. Shake hard for 15 seconds until chilled.\n3. Fine strain into a chilled coupe glass.\n4. Garnish with a flamed orange twist.",
            baseSpirit = "Vodka",
            ingredients = listOf(
                InitialIngredient("Vodka", "1.5 oz", "Spirits"),
                InitialIngredient("Cointreau", "3/4 oz", "Liqueurs & Fortified"),
                InitialIngredient("Fresh Lime Juice", "1/2 oz", "Citrus & Produce"),
                InitialIngredient("Cranberry Juice", "1/2 oz", "Mixers & Juices"),
                InitialIngredient("Orange Peel", "1 twist", "Garnishes & Ice", isOptional = true)
            )
        ),
        InitialRecipe(
            name = "Aperol Spritz",
            description = "The ultimate Italian aperitivo - effervescent prosecco, bitter aperol, and sparkling soda.",
            glassType = "Wine Glass",
            ice = "Cubed Ice",
            garnish = "Orange Slice",
            instructions = "1. Fill a large wine glass with ice cubes.\n2. Pour in Prosecco followed by Aperol.\n3. Add a splash of Club Soda and stir gently once.\n4. Garnish with a fresh orange slice.",
            baseSpirit = "Prosecco",
            ingredients = listOf(
                InitialIngredient("Prosecco", "3 oz", "Liqueurs & Fortified"),
                InitialIngredient("Aperol", "2 oz", "Liqueurs & Fortified"),
                InitialIngredient("Club Soda", "1 splash", "Mixers & Juices"),
                InitialIngredient("Orange Peel", "1 slice", "Garnishes & Ice")
            )
        ),
        InitialRecipe(
            name = "Tom Collins",
            description = "A long, crisp botanical lemonade made with gin, fresh lemon, sugar, and sparkling water.",
            glassType = "Collins / Highball Glass",
            ice = "Cubed Ice",
            garnish = "Lemon Wheel & Maraschino Cherry",
            instructions = "1. Combine Gin, Fresh Lemon Juice, and Simple Syrup in a shaker with ice.\n2. Shake and strain into a highball glass filled with fresh ice.\n3. Top with Club Soda and stir gently.\n4. Garnish with a lemon wheel and cherry.",
            baseSpirit = "Gin",
            ingredients = listOf(
                InitialIngredient("Gin", "2 oz", "Spirits"),
                InitialIngredient("Fresh Lemon Juice", "1 oz", "Citrus & Produce"),
                InitialIngredient("Simple Syrup", "1/2 oz", "Bitters & Syrups"),
                InitialIngredient("Club Soda", "3 oz", "Mixers & Juices"),
                InitialIngredient("Lemon Twist", "1 wheel", "Garnishes & Ice")
            )
        ),
        InitialRecipe(
            name = "Manhattan",
            description = "A robust cocktail combining rye whiskey, rich sweet vermouth, and aromatic bitters.",
            glassType = "Coupe Glass",
            ice = "None (Chilled)",
            garnish = "Maraschino Cherry",
            instructions = "1. Combine Rye Whiskey, Sweet Vermouth, and Angostura Bitters in a mixing glass with ice.\n2. Stir smoothly for 30 seconds until cold.\n3. Strain into a pre-chilled coupe glass.\n4. Garnish with a cocktail cherry.",
            baseSpirit = "Bourbon",
            ingredients = listOf(
                InitialIngredient("Bourbon", "2 oz", "Spirits"),
                InitialIngredient("Sweet Vermouth", "1 oz", "Liqueurs & Fortified"),
                InitialIngredient("Angostura Bitters", "2 dashes", "Bitters & Syrups"),
                InitialIngredient("Cherry", "1 cherry", "Garnishes & Ice", isOptional = true)
            )
        ),
        InitialRecipe(
            name = "Paloma",
            description = "Mexico's favorite highball - juicy grapefruit, tart lime, and smooth tequila topped with soda.",
            glassType = "Highball Glass",
            ice = "Cubed Ice",
            garnish = "Grapefruit Wedge & Salt Rim",
            instructions = "1. Rim half of a highball glass with salt.\n2. Add Tequila, Fresh Lime Juice, Grapefruit Juice, and Agave Nectar into the glass with ice.\n3. Top with Club Soda and stir lightly.\n4. Garnish with a fresh grapefruit wedge.",
            baseSpirit = "Tequila",
            ingredients = listOf(
                InitialIngredient("Tequila", "2 oz", "Spirits"),
                InitialIngredient("Fresh Lime Juice", "1/2 oz", "Citrus & Produce"),
                InitialIngredient("Grapefruit Juice", "2 oz", "Mixers & Juices"),
                InitialIngredient("Agave Nectar", "1/4 oz", "Bitters & Syrups"),
                InitialIngredient("Club Soda", "2 oz", "Mixers & Juices"),
                InitialIngredient("Salt", "For rim", "Garnishes & Ice", isOptional = true)
            )
        ),
        InitialRecipe(
            name = "Mint Julep",
            description = "The iconic Southern refresher packed with bourbon, muddled mint, and heaped crushed ice.",
            glassType = "Julep Cup / Rocks Glass",
            ice = "Crushed Ice",
            garnish = "Generous Mint Plume",
            instructions = "1. In a Julep cup, gently press mint leaves with Simple Syrup to release essential oils.\n2. Add Bourbon and fill cup 2/3 full with crushed ice.\n3. Stir briskly until frost forms on the metal cup exterior.\n4. Mound more crushed ice on top like a snow cone and garnish with mint sprigs.",
            baseSpirit = "Bourbon",
            ingredients = listOf(
                InitialIngredient("Bourbon", "2.5 oz", "Spirits"),
                InitialIngredient("Simple Syrup", "1/2 oz", "Bitters & Syrups"),
                InitialIngredient("Mint Leaves", "8-10 leaves", "Citrus & Produce")
            )
        )
    )
}
