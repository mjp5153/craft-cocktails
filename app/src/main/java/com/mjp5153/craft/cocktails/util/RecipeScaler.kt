package com.mjp5153.craft.cocktails.util

import java.util.Locale

object RecipeScaler {
    /**
     * Scales an ingredient amount string (e.g. "2 oz", "3/4 oz", "1 1/2 oz", "2 dashes")
     * by a multiplier (e.g. 2.0f for 2x).
     */
    fun scaleAmount(amountStr: String, multiplier: Float): String {
        if (multiplier == 1.0f || amountStr.isBlank()) return amountStr

        // Regular expression to extract quantity and remaining unit/text
        // Group 1: Mixed fraction e.g. "1 1/2" or fraction "3/4" or decimal "1.5" or integer "2"
        // Group 2: Unit / rest of string e.g. "oz", "dashes", "tsp"
        val regex = Regex("""^(\d+\s+\d+/\d+|\d+/\d+|\d+(?:\.\d+)?)\s*(.*)$""", RegexOption.IGNORE_CASE)
        val match = regex.find(amountStr.trim()) ?: return amountStr

        val rawQuantity = match.groupValues[1]
        val unit = match.groupValues[2]

        val numericValue = parseQuantity(rawQuantity) ?: return amountStr
        val scaledValue = numericValue * multiplier

        val formattedQuantity = formatQuantity(scaledValue)
        val adjustedUnit = adjustUnitPlural(unit, scaledValue)

        return if (adjustedUnit.isBlank()) formattedQuantity else "$formattedQuantity $adjustedUnit"
    }

    private fun parseQuantity(str: String): Double? {
        val trimmed = str.trim()
        // Check mixed fraction like "1 1/2"
        if (trimmed.contains(" ") && trimmed.contains("/")) {
            val parts = trimmed.split("\\s+".toRegex())
            if (parts.size == 2) {
                val whole = parts[0].toDoubleOrNull() ?: 0.0
                val frac = parseFraction(parts[1]) ?: 0.0
                return whole + frac
            }
        }
        // Check single fraction like "3/4"
        if (trimmed.contains("/")) {
            return parseFraction(trimmed)
        }
        // Check decimal or int
        return trimmed.toDoubleOrNull()
    }

    private fun parseFraction(str: String): Double? {
        val parts = str.split("/")
        if (parts.size == 2) {
            val num = parts[0].toDoubleOrNull()
            val den = parts[1].toDoubleOrNull()
            if (num != null && den != null && den != 0.0) {
                return num / den
            }
        }
        return null
    }

    private fun formatQuantity(value: Double): String {
        val whole = value.toInt()
        val frac = value - whole

        // If very close to integer
        if (Math.abs(frac) < 0.05) {
            return whole.toString()
        }
        if (Math.abs(frac - 1.0) < 0.05) {
            return (whole + 1).toString()
        }

        // Common bartending fractions
        val fractionStr = when {
            Math.abs(frac - 0.25) < 0.06 -> "1/4"
            Math.abs(frac - 0.333) < 0.06 -> "1/3"
            Math.abs(frac - 0.5) < 0.06 -> "1/2"
            Math.abs(frac - 0.666) < 0.06 -> "2/3"
            Math.abs(frac - 0.75) < 0.06 -> "3/4"
            else -> null
        }

        return if (fractionStr != null) {
            if (whole > 0) "$whole $fractionStr" else fractionStr
        } else {
            // Decimal representation with up to 2 decimal places
            String.format(Locale.US, "%.2f", value).trimEnd('0').trimEnd('.')
        }
    }

    private fun adjustUnitPlural(unit: String, value: Double): String {
        if (unit.isBlank()) return ""
        val isPlural = value > 1.01
        var u = unit
        if (isPlural) {
            if (u.equals("dash", ignoreCase = true)) u = "dashes"
            else if (u.equals("splash", ignoreCase = true)) u = "splashes"
            else if (u.equals("drop", ignoreCase = true)) u = "drops"
            else if (u.equals("leaf", ignoreCase = true)) u = "leaves"
            else if (u.equals("wedge", ignoreCase = true)) u = "wedges"
            else if (u.equals("barspoon", ignoreCase = true)) u = "barspoons"
        } else if (value <= 1.01 && value >= 0.99) {
            if (u.equals("dashes", ignoreCase = true)) u = "dash"
            else if (u.equals("splashes", ignoreCase = true)) u = "splash"
            else if (u.equals("drops", ignoreCase = true)) u = "drop"
            else if (u.equals("leaves", ignoreCase = true)) u = "leaf"
            else if (u.equals("wedges", ignoreCase = true)) u = "wedge"
            else if (u.equals("barspoons", ignoreCase = true)) u = "barspoon"
        }
        return u
    }
}
