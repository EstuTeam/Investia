package com.midastrading.app.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.midastrading.app.presentation.navigation.Screen
import com.midastrading.app.presentation.theme.*

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(MidasPrimary, MidasAccent),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ShowChart, null, modifier = Modifier.size(36.dp), tint = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Investia",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Hesabınıza giriş yapın",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(Icons.Filled.Email, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MidasPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MidasPrimary
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(Icons.Filled.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        null, tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MidasPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MidasPrimary
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login button
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MidasPrimary),
            enabled = !state.isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (state.isLoading) {
                Text("Giriş yapılıyor...", color = Color.White, style = MaterialTheme.typography.labelLarge)
            } else {
                Text("Giriş Yap", style = MaterialTheme.typography.labelLarge)
            }
        }

        if (state.error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = state.error!!,
                style = MaterialTheme.typography.bodySmall,
                color = LossRed,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
            Text("Hesabın yok mu? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Kayıt Ol", color = MidasPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MidasPrimary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MidasPrimary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(MidasSecondary, MidasAccent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.PersonAdd, null, modifier = Modifier.size(32.dp), tint = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Hesap Oluştur", style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = fullName, onValueChange = { fullName = it },
            label = { Text("Ad Soyad", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("E-posta", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(Icons.Filled.Email, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Şifre", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(Icons.Filled.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        null, tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.register(email, password, fullName) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MidasPrimary),
            enabled = !state.isLoading && email.isNotBlank() && password.isNotBlank() && fullName.isNotBlank()
        ) {
            if (state.isLoading) {
                Text("Kayıt olunuyor...", color = Color.White, style = MaterialTheme.typography.labelLarge)
            } else {
                Text("Kayıt Ol", style = MaterialTheme.typography.labelLarge)
            }
        }

        if (state.error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(state.error!!, style = MaterialTheme.typography.bodySmall,
                color = LossRed, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Zaten hesabın var mı? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Giriş Yap", color = MidasPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}
