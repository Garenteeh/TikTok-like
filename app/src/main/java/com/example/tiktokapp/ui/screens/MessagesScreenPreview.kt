package com.example.tiktokapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.tiktokapp.data.db.entities.MessageEntity
import com.example.tiktokapp.data.db.entities.SubjectEntity
import com.example.tiktokapp.ui.theme.TikTokAppTheme
import com.example.tiktokapp.viewModels.states.MessageUiState

@Preview(showBackground = true, name = "Conversation - Vide", heightDp = 600)
@Composable
private fun MessagesScreenPreviewEmpty() {
    TikTokAppTheme {
        MessagesContent(
            uiState = MessageUiState(
                messages = emptyList(),
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

@Preview(showBackground = true, name = "Conversation - Loading", heightDp = 600)
@Composable
private fun MessagesScreenPreviewLoading() {
    TikTokAppTheme {
        MessagesContent(
            uiState = MessageUiState(
                messages = emptyList(),
                subject = SubjectEntity(
                    id = "1",
                    user = "Marie Martin",
                    title = "Chargement...",
                    timestamp = System.currentTimeMillis()
                ),
                isLoading = true,
                error = null
            ),
            currentUserId = "currentUser"
        )
    }
}

@Preview(showBackground = true, name = "Conversation - Erreur", heightDp = 600)
@Composable
private fun MessagesScreenPreviewError() {
    TikTokAppTheme {
        MessagesContent(
            uiState = MessageUiState(
                messages = emptyList(),
                subject = SubjectEntity(
                    id = "1",
                    user = "Pierre Durand",
                    title = "Problème de connexion",
                    timestamp = System.currentTimeMillis()
                ),
                isLoading = false,
                error = "Impossible de charger les messages"
            ),
            currentUserId = "currentUser"
        )
    }
}

@Preview(showBackground = true, name = "Conversation - Courte", heightDp = 700)
@Composable
private fun MessagesScreenPreviewShort() {
    TikTokAppTheme {
        MessagesContent(
            uiState = MessageUiState(
                messages = listOf(
                    MessageEntity(
                        id = "1",
                        subjectId = "s1",
                        user = "Jean Dupont",
                        content = "Salut !",
                        timestamp = System.currentTimeMillis() - 3600000
                    ),
                    MessageEntity(
                        id = "2",
                        subjectId = "s1",
                        user = "currentUser",
                        content = "Salut Jean ! Comment ça va ?",
                        timestamp = System.currentTimeMillis() - 3000000
                    ),
                    MessageEntity(
                        id = "3",
                        subjectId = "s1",
                        user = "Jean Dupont",
                        content = "Très bien merci !",
                        timestamp = System.currentTimeMillis() - 1800000
                    )
                ),
                subject = SubjectEntity(
                    id = "s1",
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

@Preview(showBackground = true, name = "Conversation - Longue", heightDp = 800)
@Composable
private fun MessagesScreenPreviewLong() {
    TikTokAppTheme {
        MessagesContent(
            uiState = MessageUiState(
                messages = listOf(
                    MessageEntity(
                        id = "1",
                        subjectId = "s1",
                        user = "Marie Martin",
                        content = "Bonjour ! Je voulais discuter du projet",
                        timestamp = System.currentTimeMillis() - 7200000
                    ),
                    MessageEntity(
                        id = "2",
                        subjectId = "s1",
                        user = "currentUser",
                        content = "Bonjour Marie ! Oui bien sûr",
                        timestamp = System.currentTimeMillis() - 7000000
                    ),
                    MessageEntity(
                        id = "3",
                        subjectId = "s1",
                        user = "Marie Martin",
                        content = "J'ai quelques questions sur l'architecture",
                        timestamp = System.currentTimeMillis() - 6800000
                    ),
                    MessageEntity(
                        id = "4",
                        subjectId = "s1",
                        user = "currentUser",
                        content = "Pas de problème, qu'est-ce que tu veux savoir ?",
                        timestamp = System.currentTimeMillis() - 6600000
                    ),
                    MessageEntity(
                        id = "5",
                        subjectId = "s1",
                        user = "Marie Martin",
                        content = "Est-ce qu'on utilise MVVM ou MVI pour ce projet ?",
                        timestamp = System.currentTimeMillis() - 6400000
                    ),
                    MessageEntity(
                        id = "6",
                        subjectId = "s1",
                        user = "currentUser",
                        content = "On utilise MVVM avec les ViewModels et StateFlow",
                        timestamp = System.currentTimeMillis() - 6200000
                    ),
                    MessageEntity(
                        id = "7",
                        subjectId = "s1",
                        user = "Marie Martin",
                        content = "D'accord ! Et pour la base de données ?",
                        timestamp = System.currentTimeMillis() - 6000000
                    ),
                    MessageEntity(
                        id = "8",
                        subjectId = "s1",
                        user = "currentUser",
                        content = "Room avec des entités et des DAOs",
                        timestamp = System.currentTimeMillis() - 5800000
                    ),
                    MessageEntity(
                        id = "9",
                        subjectId = "s1",
                        user = "Marie Martin",
                        content = "Parfait ! Merci pour les précisions",
                        timestamp = System.currentTimeMillis() - 5600000
                    ),
                    MessageEntity(
                        id = "10",
                        subjectId = "s1",
                        user = "currentUser",
                        content = "De rien ! N'hésite pas si tu as d'autres questions",
                        timestamp = System.currentTimeMillis() - 300000
                    )
                ),
                subject = SubjectEntity(
                    id = "s1",
                    user = "Marie Martin",
                    title = "Questions architecture",
                    timestamp = System.currentTimeMillis()
                ),
                isLoading = false,
                error = null
            ),
            currentUserId = "currentUser"
        )
    }
}

@Preview(showBackground = true, name = "Conversation - Messages longs", heightDp = 800)
@Composable
private fun MessagesScreenPreviewLongMessages() {
    TikTokAppTheme {
        MessagesContent(
            uiState = MessageUiState(
                messages = listOf(
                    MessageEntity(
                        id = "1",
                        subjectId = "s1",
                        user = "Pierre Durand",
                        content = "Salut ! J'ai une question à propos de l'implémentation de la fonctionnalité de messages privés. Est-ce que tu penses qu'on devrait utiliser WebSocket pour le temps réel ou est-ce que tu préfères une approche avec polling ?",
                        timestamp = System.currentTimeMillis() - 1800000
                    ),
                    MessageEntity(
                        id = "2",
                        subjectId = "s1",
                        user = "currentUser",
                        content = "Bonne question ! Je pense que WebSocket serait plus efficace pour le temps réel. On pourrait utiliser Socket.io ou une autre bibliothèque. Le polling consomme plus de ressources et la latence est plus élevée.",
                        timestamp = System.currentTimeMillis() - 1200000
                    ),
                    MessageEntity(
                        id = "3",
                        subjectId = "s1",
                        user = "Pierre Durand",
                        content = "D'accord, je suis du même avis. Je vais commencer à regarder comment intégrer ça dans notre architecture actuelle.",
                        timestamp = System.currentTimeMillis() - 600000
                    ),
                    MessageEntity(
                        id = "4",
                        subjectId = "s1",
                        user = "currentUser",
                        content = "Super ! Tiens-moi au courant si tu rencontres des problèmes.",
                        timestamp = System.currentTimeMillis() - 180000
                    )
                ),
                subject = SubjectEntity(
                    id = "s1",
                    user = "Pierre Durand",
                    title = "WebSocket vs Polling",
                    timestamp = System.currentTimeMillis()
                ),
                isLoading = false,
                error = null
            ),
            currentUserId = "currentUser"
        )
    }
}
