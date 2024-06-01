package com.itami.calorie_tracker.authentication_feature.presentation.screens.reset_password

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.itami.calorie_tracker.R
import com.itami.calorie_tracker.core.presentation.components.OtpTextField
import com.itami.calorie_tracker.core.presentation.components.OutlinedTextField
import com.itami.calorie_tracker.core.presentation.state.PasswordTextFieldState
import com.itami.calorie_tracker.core.presentation.state.StandardTextFieldState
import com.itami.calorie_tracker.core.presentation.theme.CalorieTrackerTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun ResetPasswordScreen(
    onNavigateToResetPasswordSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
    state: ResetPasswordState,
    uiEvent: Flow<ResetPasswordUiEvent>,
    onAction: (ResetPasswordAction) -> Unit,
) {
    LaunchedEffect(key1 = Unit) {
        uiEvent.collect { event ->
            when (event) {
                is ResetPasswordUiEvent.PasswordResetSuccessful -> {
                    onNavigateToResetPasswordSuccess()
                }

                is ResetPasswordUiEvent.ShowSnackbar -> {
                    onShowSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        containerColor = CalorieTrackerTheme.colors.background,
        contentColor = CalorieTrackerTheme.colors.onBackground,
        topBar = {
            TopBarSection(onNavigateBack = onNavigateBack)
        }
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .padding(scaffoldPadding)
                .padding(horizontal = CalorieTrackerTheme.padding.medium)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(top = CalorieTrackerTheme.padding.extraLarge)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OtpCodeSection(
                    otpState = state.codeState,
                    email = state.email,
                    isLoading = state.isLoading,
                    onOtpChange = {
                        onAction(ResetPasswordAction.CodeInputChange(it))
                    }
                )
                Spacer(modifier = Modifier.height(100.dp))
                NewPasswordSection(
                    passwordState = state.passwordState,
                    onPasswordChange = {
                        onAction(ResetPasswordAction.PasswordInputChange(it))
                    },
                    onPasswordVisibilityChange = {
                        onAction(ResetPasswordAction.PasswordVisibilityChange(it))
                    },
                    repeatPasswordState = state.repeatPasswordState,
                    onRepeatPasswordChange = {
                        onAction(ResetPasswordAction.RepeatPasswordInputChange(it))
                    },
                    onRepeatPasswordVisibilityChange = {
                        onAction(ResetPasswordAction.RepeatPasswordVisibilityChange(it))
                    },
                    isLoading = state.isLoading
                )
            }
            ResetButtonSection(
                isLoading = state.isLoading,
                onResetClick = {
                    onAction(ResetPasswordAction.ResetPassword)
                }
            )
            if (state.isLoading) {
                CircularProgressIndicator(color = CalorieTrackerTheme.colors.primary)
            }
        }
    }
}

@Composable
private fun ColumnScope.OtpCodeSection(
    otpState: StandardTextFieldState,
    email: String,
    isLoading: Boolean,
    emailColor: Color = CalorieTrackerTheme.colors.primary,
    textColor: Color = CalorieTrackerTheme.colors.onBackgroundVariant,
    onOtpChange: (String) -> Unit,
) {
    val text = stringResource(R.string.enter_password_reset_code, email)

    val annotatedText = remember {
        buildAnnotatedString {
            val startIndex = text.indexOf(email)
            val endIndex = startIndex + email.length
            append(text.substring(0, startIndex))
            withStyle(style = SpanStyle(color = emailColor, fontWeight = FontWeight.Medium)) {
                append(email)
            }
            append(text.substring(endIndex))
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = CalorieTrackerTheme.padding.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = annotatedText,
            color = textColor,
            style = CalorieTrackerTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(CalorieTrackerTheme.spacing.large))
        OtpTextField(
            otpText = otpState.text,
            modifier = Modifier.fillMaxWidth(),
            otpCount = 6,
            enabled = !isLoading,
            onOtpTextChange = onOtpChange
        )
    }
}

@Composable
private fun ColumnScope.NewPasswordSection(
    passwordState: PasswordTextFieldState,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: (isVisible: Boolean) -> Unit,
    repeatPasswordState: PasswordTextFieldState,
    onRepeatPasswordChange: (String) -> Unit,
    onRepeatPasswordVisibilityChange: (isVisible: Boolean) -> Unit,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = passwordState.text,
            onValueChange = onPasswordChange,
            enabled = !isLoading,
            label = stringResource(R.string.label_password),
            error = passwordState.errorMessage,
            keyboardType = KeyboardType.Password,
            visualTransformation = if (passwordState.isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        onPasswordVisibilityChange(!passwordState.isPasswordVisible)
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordState.isPasswordVisible) R.drawable.icon_visibility
                            else R.drawable.icon_visibility_off
                        ),
                        contentDescription = stringResource(R.string.desc_visibility_icon)
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(CalorieTrackerTheme.spacing.medium))
        OutlinedTextField(
            value = repeatPasswordState.text,
            onValueChange = onRepeatPasswordChange,
            enabled = !isLoading,
            label = stringResource(R.string.label_repeat_password),
            error = repeatPasswordState.errorMessage,
            keyboardType = KeyboardType.Password,
            visualTransformation = if (repeatPasswordState.isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        onRepeatPasswordVisibilityChange(!repeatPasswordState.isPasswordVisible)
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (repeatPasswordState.isPasswordVisible) R.drawable.icon_visibility
                            else R.drawable.icon_visibility_off
                        ),
                        contentDescription = stringResource(R.string.desc_visibility_icon)
                    )
                }
            }
        )
    }
}

@Composable
private fun BoxScope.ResetButtonSection(
    isLoading: Boolean,
    onResetClick: () -> Unit,
) {
    Button(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(bottom = CalorieTrackerTheme.padding.extraLarge),
        colors = ButtonDefaults.buttonColors(
            containerColor = CalorieTrackerTheme.colors.primary,
            contentColor = CalorieTrackerTheme.colors.onPrimary,
        ),
        contentPadding = PaddingValues(
            vertical = CalorieTrackerTheme.padding.default
        ),
        shape = CalorieTrackerTheme.shapes.small,
        enabled = !isLoading,
        onClick = onResetClick,
    ) {
        Text(
            text = stringResource(R.string.reset),
            style = CalorieTrackerTheme.typography.titleSmall,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarSection(
    onNavigateBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.padding(top = CalorieTrackerTheme.padding.small),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = CalorieTrackerTheme.colors.background,
            titleContentColor = CalorieTrackerTheme.colors.onBackground,
            navigationIconContentColor = CalorieTrackerTheme.colors.onBackground
        ),
        title = {
            Text(
                text = stringResource(R.string.title_forgot_password),
                style = CalorieTrackerTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = CalorieTrackerTheme.colors.onBackground
            )
        },
        navigationIcon = {
            IconButton(
                modifier = Modifier,
                onClick = onNavigateBack
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_arrow_back),
                    contentDescription = stringResource(R.string.desc_icon_navigate_back),
                    tint = CalorieTrackerTheme.colors.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}