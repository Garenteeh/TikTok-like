package com.example.tiktokapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.ui.theme.TikTokAppTheme

@Composable
fun BottomBar(
    onHome: () -> Unit,
    onAdd: () -> Unit,
    onProfile: () -> Unit,
    onMessages: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
            .background(Color(0x00FFFFFF)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 30.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { onHome() }) {
                Icon(
                    Icons.Outlined.Home,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(35.dp),
                    contentDescription = "Home Button"
                )
            }

            IconButton(onClick = { onMessages() }) {
                Icon(
                    Icons.Outlined.Email,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(35.dp),
                    contentDescription = "Messages Button"
                )
            }

            IconButton(onClick = { onAdd() }) {
                Icon(
                    Icons.Outlined.Add,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(35.dp),
                    contentDescription = "Add Button"
                )
            }

            IconButton(onClick = onProfile) {
                Icon(
                    Icons.Outlined.Person,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(35.dp),
                    contentDescription = "Profile Button"
                )
            }
        }
    }
}


@Preview
@Composable
fun BottomBarPreview() {
    TikTokAppTheme() {
        BottomBar(onHome = {}, onAdd = {}, onProfile = {}, onMessages = {})
    }
}