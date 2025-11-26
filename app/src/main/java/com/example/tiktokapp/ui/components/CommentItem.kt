package com.example.tiktokapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiktokapp.domain.models.Comment
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier,
    isReply: Boolean = false
) {
    var showReplies by remember { mutableStateOf(false) }
    var visibleRepliesCount by remember { mutableStateOf(4) }

    val repliesList = comment.replies ?: emptyList()
    val hasReplies = repliesList.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isReply) 40.dp else 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 8.dp
            )
    ) {
        // Nom de l'utilisateur
        Text(
            text = comment.user,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Message du commentaire
        Text(
            text = comment.message,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.9f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Timestamp
        Text(
            text = formatTimestamp(comment.timestamp),
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.6f)
        )

        // Bouton pour afficher/masquer les réponses
        if (hasReplies && !isReply) {
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { showReplies = !showReplies },
                modifier = Modifier.padding(0.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (showReplies) {
                        "--- masquer les réponses ---"
                    } else {
                        "--- afficher les réponses ---"
                    },
                    fontSize = 13.sp,
                    color = Color(0xFF00D4FF),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Affichage des réponses
        if (showReplies && hasReplies) {
            Spacer(modifier = Modifier.height(8.dp))

            val repliesToShow = repliesList.take(visibleRepliesCount)

            repliesToShow.forEach { reply ->
                CommentItem(
                    comment = reply,
                    isReply = true
                )
            }

            // Bouton "Afficher X de plus"
            if (repliesList.size > visibleRepliesCount) {
                TextButton(
                    onClick = { visibleRepliesCount += 4 },
                    modifier = Modifier.padding(start = 40.dp, top = 4.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Afficher ${minOf(4, repliesList.size - visibleRepliesCount)} de plus",
                        fontSize = 13.sp,
                        color = Color(0xFF00D4FF),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Divider entre les commentaires (sauf pour les réponses)
        if (!isReply) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.2f),
                thickness = 0.5.dp
            )
        }
    }
}

/**
 * Formate le timestamp en format relatif (ex: "il y a 2h")
 */
private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "À l'instant"
        diff < 3_600_000 -> "Il y a ${diff / 60_000}min"
        diff < 86_400_000 -> "Il y a ${diff / 3_600_000}h"
        diff < 604_800_000 -> "Il y a ${diff / 86_400_000}j"
        else -> {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            sdf.format(Date(timestamp))
        }
    }
}

