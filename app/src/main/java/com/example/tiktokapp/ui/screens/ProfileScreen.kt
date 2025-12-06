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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiktokapp.ui.components.BottomBar
import com.example.tiktokapp.domain.models.User

@Composable
fun ProfileScreen(
    user: User?,
    onLogout: () -> Unit,
    onHome: () -> Unit = {},
    onAdd: () -> Unit = {}
) {
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomBar(onHome = { onHome() }, onAdd = { onAdd() }, onProfile = { /* current */ })
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
                    text = "@${user?.username ?: ""}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Nom complet
                Text(
                    text = "${user?.firstName ?: ""} ${user?.lastName ?: ""}",
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
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        ProfileInfoItem(label = "Email", value = user?.email ?: "")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ProfileInfoItem(label = "Téléphone", value = user?.phoneNumber ?: "")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ProfileInfoItem(label = "Date de naissance", value = user?.birthDate ?: "")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ProfileInfoItem(label = "Pays", value = user?.country ?: "")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bouton de déconnexion
                Button(
                    onClick = {
                        onLogout()
                        Toast.makeText(context, "Déconnecté", Toast.LENGTH_SHORT).show()
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
