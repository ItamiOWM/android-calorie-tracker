package com.itami.calorie_tracker.authentication_feature.presentation.screens.login_email

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itami.calorie_tracker.R
import com.itami.calorie_tracker.core.domain.model.Theme
import com.itami.calorie_tracker.core.presentation.components.ObserveAsEvents
import com.itami.calorie_tracker.core.presentation.components.OutlinedTextField
import com.itami.calorie_tracker.core.presentation.state.PasswordTextFieldState
import com.itami.calorie_tracker.core.presentation.state.StandardTextFieldState
import com.itami.calorie_tracker.core.presentation.theme.CalorieTrackerTheme

@Composable
fun LoginEmailScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateBack: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
    viewModel: LoginEmailViewModel = hiltViewModel(),
) {
    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is LoginEmailUiEvent.LoginSuccessful -> onLoginSuccess()
            is LoginEmailUiEvent.ShowSnackbar -> onShowSnackbar(event.message)
            is LoginEmailUiEvent.NavigateBack -> onNavigateBack()
            is LoginEmailUiEvent.NavigateToForgotPassword -> onNavigateToForgotPassword()
        }
    }

    LoginEmailScreenContent(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Preview
@Composable
fun LoginScreenContentPreview() {
    CalorieTrackerTheme(theme = Theme.SYSTEM_THEME) {
        LoginEmailScreenContent(
            state = LoginEmailState(),
            onAction = {}
        )
    }
}

@Composable
private fun LoginEmailScreenContent(
    state: LoginEmailState,
    onAction: (LoginEmailAction) -> Unit,
) {
    Scaffold(
        containerColor = CalorieTrackerTheme.colors.background,
        contentColor = CalorieTrackerTheme.colors.onBackground,
        topBar = {
            TopBarSection(
                onNavigateBackClick = {
                    onAction(LoginEmailAction.NavigateBackClick)
                }
            )
        },
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize()
                .padding(horizontal = CalorieTrackerTheme.padding.medium),
            contentAlignment = Alignment.Center,
        ) {
            CredentialsSection(
                modifier = Modifier
                    .padding(bottom = CalorieTrackerTheme.padding.medium)
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                emailState = state.emailState,
                onEmailChange = {
                    onAction(LoginEmailAction.EmailInputChange(it))
                },
                passwordState = state.passwordState,
                onPasswordChange = {
                    onAction(LoginEmailAction.PasswordInputChange(it))
                },
                onPasswordVisibilityIconClick = {
                    onAction(LoginEmailAction.PasswordVisibilityIconClick)
                },
                isLoading = state.isLoading
            )
            LoginButtonSection(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = CalorieTrackerTheme.padding.large),
                isLoading = state.isLoading,
                onLoginButtonClick = {
                    onAction(LoginEmailAction.LoginClick)
                },
                onForgotPasswordClick = {
                    onAction(LoginEmailAction.ForgotPasswordClick)
                }
            )
            if (state.isLoading) {
                CircularProgressIndicator(color = CalorieTrackerTheme.colors.primary)
            }
        }
    }
}

@Composable
private fun LoginButtonSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onLoginButtonClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = CalorieTrackerTheme.colors.primary,
                contentColor = CalorieTrackerTheme.colors.onPrimary,
            ),
            contentPadding = PaddingValues(
                vertical = CalorieTrackerTheme.padding.default
            ),
            shape = CalorieTrackerTheme.shapes.small,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onLoginButtonClick()
            },
        ) {
            Text(
                text = stringResource(R.string.login),
                style = CalorieTrackerTheme.typography.titleSmall,
            )
        }
        Spacer(modifier = Modifier.height(CalorieTrackerTheme.spacing.medium))
        TextButton(
            onClick = onForgotPasswordClick,
            enabled = !isLoading,
            contentPadding = PaddingValues(CalorieTrackerTheme.padding.tiny)
        ) {
            Text(
                text = stringResource(R.string.forgot_password_qm),
                color = CalorieTrackerTheme.colors.primary,
                style = CalorieTrackerTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun CredentialsSection(
    modifier: Modifier,
    emailState: StandardTextFieldState,
    onEmailChange: (String) -> Unit,
    passwordState: PasswordTextFieldState,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityIconClick: () -> Unit,
    isLoading: Boolean,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CalorieTrackerTheme.spacing.medium)
    ) {
        OutlinedTextField(
            value = emailState.text,
            onValueChange = onEmailChange,
            enabled = !isLoading,
            label = stringResource(R.string.label_email),
            error = emailState.errorMessage,
            keyboardType = KeyboardType.Email
        )
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
                    onClick = onPasswordVisibilityIconClick
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarSection(
    onNavigateBackClick: () -> Unit,
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
                text = stringResource(R.string.login),
                style = CalorieTrackerTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = CalorieTrackerTheme.colors.onBackground
            )
        },
        navigationIcon = {
            IconButton(
                modifier = Modifier,
                onClick = onNavigateBackClick
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