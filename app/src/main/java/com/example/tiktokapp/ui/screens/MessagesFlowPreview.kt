package com.example.tiktokapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.data.db.entities.SubjectEntity
import com.example.tiktokapp.ui.theme.TikTokAppTheme
import com.example.tiktokapp.viewModels.states.MessageUiState
import com.example.tiktokapp.viewModels.states.SubjectUiState

@Preview(showBackground = true, name = "Flow - Liste + Conversation", widthDp = 800, heightDp = 600)
@Composable
private fun MessagesFlowPreview() {
    TikTokAppTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                SubjectsListContent(
                    uiState = SubjectUiState(
                        subjects = listOf(
                            SubjectEntity(
                                id = "1",
                                user = "Jean Dupont",
                                title = "Discussion projet",
                                timestamp = System.currentTimeMillis() - 300000
                            ),
                            SubjectEntity(
                                id = "2",
                                user = "Marie Martin",
                                title = "Réunion d'équipe",
                                timestamp = System.currentTimeMillis() - 3600000
                            ),
                            SubjectEntity(
                                id = "3",
                                user = "Pierre Durand",
                                title = "Question technique",
                                timestamp = System.currentTimeMillis() - 86400000
                            )
                        ),
                        isLoading = false,
                        error = null
                    ),
                    onSubjectClick = {}
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline
            )

            Box(modifier = Modifier.weight(1f)) {
                MessagesContent(
                    uiState = MessageUiState(
                        messages = listOf(
                            MessageEntity(
                                id = "1",
                                subjectId = "1",
                                user = "Jean Dupont",
                                content = "Salut ! Comment avance le projet ?",
                                timestamp = System.currentTimeMillis() - 300000
                            ),
                            MessageEntity(
                                id = "2",
                                subjectId = "1",
                                user = "currentUser",
                                content = "Bien ! J'ai presque terminé la partie UI",
                                timestamp = System.currentTimeMillis() - 180000
                            ),
                            MessageEntity(
                                id = "3",
                                subjectId = "1",
                                user = "Jean Dupont",
                                content = "Super ! Tu peux me montrer ?",
                                timestamp = System.currentTimeMillis() - 60000
                            )
                        ),
                        subject = SubjectEntity(
                            id = "1",
                            user = "Jean Dupont",
                            title = "Discussion projet",
                            timestamp = System.currentTimeMillis()
                        ),
                        isLoading = false,
                        error = null
                    ),
                    currentUserId = "currentUser"
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Navigation - Étape 1: Liste", device = "spec:width=411dp,height=891dp")
@Composable
private fun Step1SubjectsList() {
    TikTokAppTheme {
        SubjectsListContent(
            uiState = SubjectUiState(
                subjects = listOf(
                    SubjectEntity(
                        id = "1",
                        user = "Jean Dupont",
                        title = "Discussion projet",
                        timestamp = System.currentTimeMillis() - 300000
                    ),
                    SubjectEntity(
                        id = "2",
                        user = "Marie Martin",
                        title = "Réunion d'équipe",
                        timestamp = System.currentTimeMillis() - 3600000
                    )
                ),
                isLoading = false,
                error = null
            ),
            onSubjectClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Navigation - Étape 2: Conversation", device = "spec:width=411dp,height=891dp")
@Composable
private fun Step2Conversation() {
    TikTokAppTheme {
        MessagesContent(
            uiState = MessageUiState(
                messages = listOf(
                    MessageEntity(
                        id = "1",
                        subjectId = "1",
                        user = "Jean Dupont",
                        content = "Salut ! Comment avance le projet ?",
                        timestamp = System.currentTimeMillis() - 300000
                    ),
                    MessageEntity(
                        id = "2",
                        subjectId = "1",
                        user = "currentUser",
                        content = "Bien ! J'ai presque terminé la partie UI",
                        timestamp = System.currentTimeMillis() - 180000
                    ),
                    MessageEntity(
                        id = "3",
                        subjectId = "1",
                        user = "Jean Dupont",
                        content = "Super ! Tu as besoin d'aide ?",
                        timestamp = System.currentTimeMillis() - 60000
                    )
                ),
                subject = SubjectEntity(
                    id = "1",
                    user = "Jean Dupont",
                    title = "Discussion projet",
                    timestamp = System.currentTimeMillis()
                ),
                isLoading = false,
                error = null
            ),
            currentUserId = "currentUser"
        )
    }
}
