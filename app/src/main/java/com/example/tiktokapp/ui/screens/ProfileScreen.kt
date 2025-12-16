package com.example.tiktokapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiktokapp.domain.models.User
import com.example.tiktokapp.domain.models.flagEmoji
import com.example.tiktokapp.repositories.CountryRepository
import com.example.tiktokapp.ui.components.BottomBar
import com.example.tiktokapp.viewModels.UserViewModel

@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    onHome: () -> Unit = {},
    onAdd: () -> Unit = {},
    onMessages: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUser by userViewModel.currentUser.collectAsState(initial = null)
    val flagEmoji = currentUser?.country?.let { countryName ->
        CountryRepository.countries.firstOrNull { it.name == countryName }?.flagEmoji()
    } ?: ""

    Scaffold(
        bottomBar = {
            BottomBar(onHome = { onHome() }, onAdd = { onAdd() }, onProfile = { /* current */ },
                onMessages = onMessages)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0288D1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nom d'utilisateur
                Text(
                    text = "@${currentUser?.username ?: ""}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Nom complet
                Text(
                    text = "${currentUser?.firstName ?: ""} ${currentUser?.lastName ?: ""}",
                    fontSize = 18.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Informations de profil
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Informations personnelles",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        ProfileInfoItem(label = "Email", value = currentUser?.email ?: "")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ProfileInfoItem(
                            label = "Téléphone",
                            value = formatPhoneNumber(currentUser?.phoneNumber ?: "")
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ProfileInfoItem(label = "Date de naissance", value = currentUser?.birthDate ?: "")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ProfileInfoItem(
                            label = "Pays",
                            value = if (flagEmoji.isNotBlank()) "${flagEmoji} ${currentUser?.country ?: ""}" else (currentUser?.country ?: "")
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bouton de déconnexion
                Button(
                    onClick = {
                        userViewModel.logout()
                        Toast.makeText(context, "Déconnecté", Toast.LENGTH_SHORT).show()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Se déconnecter", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


fun formatPhoneNumber(raw: String): String {
    if (raw.isBlank()) return ""
    // Ne garder que les chiffres
    val digits = raw.filter { it.isDigit() }
    if (digits.isEmpty()) return raw
    // Grouper par 2 caractères et joindre par un espace
    return digits.chunked(2).joinToString(" ")
}

@Composable
private fun ProfileInfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
