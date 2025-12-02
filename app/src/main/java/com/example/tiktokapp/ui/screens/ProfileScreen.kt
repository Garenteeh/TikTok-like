package com.example.tiktokapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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

    val displayName = remember { mutableStateOf(user?.username ?: "") }
    val displayEmail = remember { mutableStateOf(user?.email ?: "") }

    LaunchedEffect(user) {
        displayName.value = user?.username ?: ""
        displayEmail.value = user?.email ?: ""
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Pseudo : ${displayName.value}")
        Text(text = "Email : ${displayEmail.value}", modifier = Modifier.padding(top = 8.dp))

        Button(
            onClick = {
                // execute logout callback
                onLogout()
                Toast.makeText(context, "Déconnecté", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Se déconnecter")
        }

        OutlinedButton(
            onClick = { onHome() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Fermer")
        }

        // Bottom bar for navigation to Home/Add
        BottomBar(onHome = { onHome() }, onAdd = { onAdd() }, onProfile = { /* current */ })
    }
}
