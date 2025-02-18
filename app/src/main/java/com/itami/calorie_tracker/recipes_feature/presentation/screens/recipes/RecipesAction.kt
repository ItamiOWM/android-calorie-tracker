package com.itami.calorie_tracker.recipes_feature.presentation.screens.recipes

import com.itami.calorie_tracker.recipes_feature.domain.model.CaloriesFilter
import com.itami.calorie_tracker.recipes_feature.domain.model.Recipe
import com.itami.calorie_tracker.recipes_feature.domain.model.TimeFilter

sealed class RecipesAction {

    data object Refresh : RecipesAction()

    data object LoadNextRecipes : RecipesAction()

    data class SearchQueryChange(val newValue: String) : RecipesAction()

    data class UpdateFilters(
        val timeFilters: List<TimeFilter>,
        val caloriesFilters: List<CaloriesFilter>,
    ): RecipesAction()

    data object ProfilePictureClick : RecipesAction()

    data object FilterIconClick : RecipesAction()

    data object DismissFilterSheet : RecipesAction()

    data class RecipeClick(val recipe: Recipe) : RecipesAction()

}