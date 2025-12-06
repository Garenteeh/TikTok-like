package com.example.tiktokapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiktokapp.domain.models.Comment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    comments: List<Comment>,
    onDismiss: () -> Unit,
    onLikeClick: (String) -> Unit = {},
    onAddComment: (String) -> Unit = {},
    onReplyToComment: (String, String) -> Unit = { _, _ -> },
    onDeleteComment: (String) -> Unit = {},
    currentUsername: String = "Moi",
    videoOwner: String = "",
    modifier: Modifier = Modifier
) {
    var commentText by remember { mutableStateOf("") }
    var replyToCommentId by remember { mutableStateOf<String?>(null) }
    var replyToUsername by remember { mutableStateOf<String?>(null) }

    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val secondaryColor = MaterialTheme.colorScheme.secondary

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = surfaceColor,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(surfaceColor)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            onSurfaceColor.copy(alpha = 0.3f),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .background(surfaceColor)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${comments.size} commentaire${if (comments.size > 1) "s" else ""}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = onSurfaceColor
                    )
                }
            }

            HorizontalDivider(
                color = onSurfaceColor.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            // Indicateur de réponse
            if (replyToCommentId != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Répondre à $replyToUsername",
                        fontSize = 14.sp,
                        color = secondaryColor
                    )
                    TextButton(
                        onClick = {
                            replyToCommentId = null
                            replyToUsername = null
                        }
                    ) {
                        Text(
                            text = "Annuler",
                            fontSize = 12.sp,
                            color = onSurfaceColor.copy(alpha = 0.7f)
                        )
                    }
                }
                HorizontalDivider(
                    color = onSurfaceColor.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }

            // Liste des commentaires
            if (comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun commentaire",
                        fontSize = 16.sp,
                        color = onSurfaceColor.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(comments) { comment ->
                        CommentItem(
                            comment = comment,
                            onLikeClick = onLikeClick,
                            onReplyClick = { commentId, username ->
                                replyToCommentId = commentId
                                replyToUsername = username
                            },
                            onDeleteClick = onDeleteComment,
                            currentUsername = currentUsername,
                            videoOwner = videoOwner
                        )
                    }
                }
            }

            // Champ de saisie de commentaire
            HorizontalDivider(
                color = onSurfaceColor.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(surfaceColor)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = if (replyToCommentId != null) "Écrivez une réponse..." else "Ajouter un commentaire...",
                            color = onSurfaceColor.copy(alpha = 0.5f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = onSurfaceColor,
                        unfocusedTextColor = onSurfaceColor,
                        focusedBorderColor = secondaryColor,
                        unfocusedBorderColor = onSurfaceColor.copy(alpha = 0.3f),
                        cursorColor = secondaryColor
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            if (replyToCommentId != null) {
                                onReplyToComment(replyToCommentId!!, commentText.trim())
                                replyToCommentId = null
                                replyToUsername = null
                            } else {
                                onAddComment(commentText.trim())
                            }
                            commentText = ""
                        }
                    },
                    enabled = commentText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Envoyer",
                        tint = if (commentText.isNotBlank()) secondaryColor else onSurfaceColor.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}



