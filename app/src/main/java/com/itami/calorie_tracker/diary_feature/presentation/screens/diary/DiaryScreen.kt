package com.itami.calorie_tracker.diary_feature.presentation.screens.diary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.itami.calorie_tracker.R
import com.itami.calorie_tracker.core.domain.model.DailyNutrientsGoal
import com.itami.calorie_tracker.core.domain.model.Theme
import com.itami.calorie_tracker.core.domain.model.User
import com.itami.calorie_tracker.core.presentation.components.ObserveAsEvents
import com.itami.calorie_tracker.core.presentation.components.pull_refresh.PullRefreshIndicator
import com.itami.calorie_tracker.core.presentation.components.pull_refresh.pullRefresh
import com.itami.calorie_tracker.core.presentation.components.pull_refresh.rememberPullRefreshState
import com.itami.calorie_tracker.core.presentation.theme.CalorieTrackerTheme
import com.itami.calorie_tracker.core.utils.DateTimeUtil
import com.itami.calorie_tracker.diary_feature.presentation.components.MealItem
import com.itami.calorie_tracker.diary_feature.presentation.components.NutrientsComponent
import com.itami.calorie_tracker.diary_feature.presentation.components.WaterIntakeComponent
import com.itami.calorie_tracker.diary_feature.presentation.model.ConsumedWaterUi
import com.itami.calorie_tracker.diary_feature.presentation.model.MealUi
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DiaryScreen(
    onNavigateToMeal: (mealId: Int) -> Unit,
    onNavigateToNewMeal: (datetime: String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
    viewModel: DiaryViewModel = hiltViewModel(),
) {
    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is DiaryUiEvent.NavigateToMeal -> onNavigateToMeal(event.mealId)
            is DiaryUiEvent.NavigateToNewMeal -> onNavigateToNewMeal(event.datetime)
            is DiaryUiEvent.NavigateToProfile -> onNavigateToProfile()
            is DiaryUiEvent.ShowSnackbar -> onShowSnackbar(event.message)
        }
    }

    DiaryScreenContent(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Preview
@Composable
fun DiaryScreenContentPreview() {
    CalorieTrackerTheme(theme = Theme.SYSTEM_THEME) {
        DiaryScreenContent(
            state = DiaryState(),
            onAction = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryScreenContent(
    state: DiaryState,
    onAction: (action: DiaryAction) -> Unit,
) {
    DatePickerSection(
        showDatePicker = state.showDatePicker,
        date = state.date,
        onShowDatePicker = { show ->
            onAction(DiaryAction.ShowDatePicker(show = show))
        },
        onChangeDate = { date ->
            onAction(DiaryAction.ChangeDate(date = date))
        }
    )

    val scrollState = rememberScrollState()

    val refreshState = rememberPullRefreshState(
        refreshing = state.isRefreshingMeals,
        onRefresh = {
            onAction(DiaryAction.Refresh)
        }
    )

    Scaffold(
        containerColor = CalorieTrackerTheme.colors.background,
        contentColor = CalorieTrackerTheme.colors.onBackground,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = CalorieTrackerTheme.colors.background,
                    titleContentColor = CalorieTrackerTheme.colors.onBackground
                ),
                title = {
                    TopBarContent(
                        user = state.user,
                        date = state.date,
                        onShowDatePicker = { show ->
                            onAction(DiaryAction.ShowDatePicker(show = show))
                        },
                        onProfileImageClick = {
                            onAction(DiaryAction.ProfilePictureClick)
                        }
                    )
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .pullRefresh(refreshState, enabled = !state.isRefreshingMeals),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(
                        start = CalorieTrackerTheme.padding.default,
                        end = CalorieTrackerTheme.padding.default,
                        top = CalorieTrackerTheme.padding.large
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(CalorieTrackerTheme.spacing.medium),
            ) {
                NutrientsSection(
                    proteinsConsumed = state.consumedProteins,
                    fatsConsumed = state.consumedFats,
                    carbsConsumed = state.consumedCarbs,
                    caloriesConsumed = state.consumedCalories,
                    dailyNutrientsGoal = state.user.dailyNutrientsGoal,
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.waterTrackerEnabled) {
                    WaterIntakeSection(
                        consumedWater = state.consumedWater,
                        dailyNutrientsGoal = state.user.dailyNutrientsGoal,
                        onAddWaterClick = {
                            onAction(DiaryAction.AddConsumedWaterClick)
                        },
                        onRemoveWaterClick = {
                            onAction(DiaryAction.RemoveConsumedWaterClick)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                MealsSection(
                    meals = state.meals,
                    onMealClick = { id ->
                        onAction(DiaryAction.MealClick(id))
                    },
                    onAddMealClick = {
                        onAction(DiaryAction.NewMealClick)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (state.isLoading) {
                CircularProgressIndicator(color = CalorieTrackerTheme.colors.primary)
            }
            if (state.isLoadingMeals) {
                CircularProgressIndicator(color = CalorieTrackerTheme.colors.primary)
            }
            PullRefreshIndicator(
                refreshing = state.isRefreshingMeals,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = CalorieTrackerTheme.colors.primary,
                backgroundColor = CalorieTrackerTheme.colors.surfacePrimary
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MealsSection(
    meals: List<MealUi>,
    onMealClick: (mealId: Int) -> Unit,
    onAddMealClick: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CalorieTrackerTheme.spacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.meals),
                style = CalorieTrackerTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = CalorieTrackerTheme.colors.onBackground,
                modifier = Modifier
            )
            IconButton(
                onClick = { onAddMealClick() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_add),
                    contentDescription = stringResource(id = R.string.desc_icon_add),
                    tint = CalorieTrackerTheme.colors.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        if (meals.isNotEmpty()) {
            val mealComponentHeight = 90.dp
            val bottomSpacerHeight = 116.dp

            val lazyColumnHeight = remember(meals.size) {
                mealComponentHeight * meals.size + bottomSpacerHeight
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = lazyColumnHeight),
                verticalArrangement = Arrangement.spacedBy(CalorieTrackerTheme.spacing.small),
                horizontalAlignment = Alignment.CenterHorizontally,
                userScrollEnabled = false,
            ) {
                itemsIndexed(items = meals, key = { _, meal -> meal.id }) { index, meal ->
                    MealItem(
                        meal = meal,
                        onMealClick = { onMealClick(meal.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(mealComponentHeight)
                            .animateItemPlacement(),
                    )
                    if (index == meals.size - 1) {
                        Spacer(modifier = Modifier.height(bottomSpacerHeight))
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(CalorieTrackerTheme.spacing.tiny),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.empty),
                    style = CalorieTrackerTheme.typography.labelLarge,
                    color = CalorieTrackerTheme.colors.onBackground,
                )
                Text(
                    text = stringResource(R.string.no_meals_found),
                    style = CalorieTrackerTheme.typography.bodySmall,
                    color = CalorieTrackerTheme.colors.onBackgroundVariant,
                )
            }
        }
    }
}

@Composable
private fun WaterIntakeSection(
    consumedWater: ConsumedWaterUi?,
    dailyNutrientsGoal: DailyNutrientsGoal,
    onAddWaterClick: () -> Unit,
    onRemoveWaterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CalorieTrackerTheme.spacing.small)
    ) {
        Text(
            text = stringResource(R.string.water_intake),
            style = CalorieTrackerTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = CalorieTrackerTheme.colors.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )
        WaterIntakeComponent(
            consumedWaterMl = consumedWater?.waterMl ?: 0,
            waterMlGoal = dailyNutrientsGoal.waterMlGoal,
            lastTimeDrank = consumedWater?.timestamp?.format(
                DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            ) ?: stringResource(R.string.text_have_not_drank),
            onAddWaterClick = onAddWaterClick,
            onRemoveWaterClick = onRemoveWaterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
        )
    }
}

@Composable
private fun NutrientsSection(
    proteinsConsumed: Int,
    fatsConsumed: Int,
    carbsConsumed: Int,
    caloriesConsumed: Int,
    dailyNutrientsGoal: DailyNutrientsGoal,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CalorieTrackerTheme.spacing.small)
    ) {
        Text(
            text = stringResource(R.string.nutrients_indicator),
            style = CalorieTrackerTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = CalorieTrackerTheme.colors.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )
        NutrientsComponent(
            proteinsConsumed = proteinsConsumed,
            fatsConsumed = fatsConsumed,
            carbsConsumed = carbsConsumed,
            caloriesConsumed = caloriesConsumed,
            proteinsGoal = dailyNutrientsGoal.proteinsGoal,
            fatsGoal = dailyNutrientsGoal.fatsGoal,
            carbsGoal = dailyNutrientsGoal.carbsGoal,
            caloriesGoal = dailyNutrientsGoal.caloriesGoal,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TopBarContent(
    user: User,
    date: ZonedDateTime,
    onShowDatePicker: (show: Boolean) -> Unit,
    onProfileImageClick: () -> Unit,
) {
    val context = LocalContext.current
    val dateText = remember(date) {
        val todaysDate = DateTimeUtil.getCurrentZonedDateTime()
        if (todaysDate.toLocalDate() == date.toLocalDate()) {
            context.getString(R.string.today)
        } else {
            date.format(DateTimeFormatter.ofPattern("E d"))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CalorieTrackerTheme.padding.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CalorieTrackerTheme.spacing.small)
        ) {
            AsyncImage(
                model = user.profilePictureUrl,
                contentDescription = stringResource(R.string.desc_user_profile_picture),
                error = painterResource(id = R.drawable.unknown_person),
                placeholder = painterResource(id = R.drawable.unknown_person),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onProfileImageClick() }
            )
            Text(
                text = user.name,
                style = CalorieTrackerTheme.typography.titleSmall,
                color = CalorieTrackerTheme.colors.onBackground,
            )
        }
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CalorieTrackerTheme.spacing.extraSmall)
        ) {
            Text(
                text = dateText,
                style = CalorieTrackerTheme.typography.bodyMedium,
                color = CalorieTrackerTheme.colors.onBackground,
            )
            IconButton(
                onClick = {
                    onShowDatePicker(true)
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_calendar),
                    contentDescription = stringResource(R.string.desc_calendar_icon),
                    tint = CalorieTrackerTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerSection(
    showDatePicker: Boolean,
    date: ZonedDateTime,
    onShowDatePicker: (show: Boolean) -> Unit,
    onChangeDate: (date: ZonedDateTime) -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.toInstant().toEpochMilli()
    )
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                onShowDatePicker(false)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { epochMilli ->
                            onChangeDate(DateTimeUtil.epochMilliToDate(epochMilli))
                        }
                        onShowDatePicker(false)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.OK),
                        style = CalorieTrackerTheme.typography.labelLarge,
                        color = CalorieTrackerTheme.colors.primary,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onShowDatePicker(false)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = CalorieTrackerTheme.typography.labelLarge,
                        color = CalorieTrackerTheme.colors.primary,
                    )
                }
            },
            shape = CalorieTrackerTheme.shapes.small,
            colors = DatePickerDefaults.colors(containerColor = CalorieTrackerTheme.colors.surfacePrimary)
        ) {
            DatePicker(
                modifier = Modifier.fillMaxWidth(),
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = CalorieTrackerTheme.colors.surfacePrimary,
                    titleContentColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                    headlineContentColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                    selectedDayContainerColor = CalorieTrackerTheme.colors.primary,
                    selectedDayContentColor = CalorieTrackerTheme.colors.onPrimary,
                    todayDateBorderColor = CalorieTrackerTheme.colors.primary,
                    todayContentColor = CalorieTrackerTheme.colors.primary,
                    dayContentColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                    currentYearContentColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                    subheadContentColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                    yearContentColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                    selectedYearContainerColor = CalorieTrackerTheme.colors.primary,
                    selectedYearContentColor = CalorieTrackerTheme.colors.onPrimary,
                    dayInSelectionRangeContentColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                    weekdayContentColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                    disabledDayContentColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                    disabledSelectedDayContainerColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                    disabledSelectedDayContentColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                    dayInSelectionRangeContainerColor = CalorieTrackerTheme.colors.onSurfacePrimary,
                ),
            )
        }
    }
}