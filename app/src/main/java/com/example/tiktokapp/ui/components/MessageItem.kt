package com.example.tiktokapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.ui.theme.TikTokAppTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageItem(
    message: MessageEntity,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                bottomEnd = if (isCurrentUser) 4.dp else 16.dp
            ),
            color = if (isCurrentUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!isCurrentUser) {
                    Text(
                        text = message.user,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrentUser) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Text(
                    text = formatMessageTime(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrentUser) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    }
                )
            }
        }
    }
}

private fun formatMessageTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "À l'instant"
        diff < 3600000 -> {
            val minutes = diff / 60000
            "Il y a $minutes minute${if (minutes > 1) "s" else ""}"
        }
        diff < 86400000 -> {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
        diff < 604800000 -> {
            val sdf = SimpleDateFormat("EEE HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
        else -> {
            val sdf = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageItemPreviewCurrentUser() {
    TikTokAppTheme {
        MessageItem(
            message = MessageEntity(
                id = "1",
                subjectId = "subject1",
                user = "Moi",
                content = "Salut ! Comment ça va ?",
                timestamp = System.currentTimeMillis() - 300000
            ),
            isCurrentUser = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageItemPreviewOtherUser() {
    TikTokAppTheme {
        MessageItem(
            message = MessageEntity(
                id = "2",
                subjectId = "subject1",
                user = "Jean Dupont",
                content = "Très bien merci ! Et toi ?",
                timestamp = System.currentTimeMillis() - 180000
            ),
            isCurrentUser = false
        )
    }
}
