package com.example.tiktokapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.data.db.entities.SubjectEntity
import com.example.tiktokapp.ui.components.SubjectListItem
import com.example.tiktokapp.ui.theme.TikTokAppTheme
import com.example.tiktokapp.viewModels.states.SubjectUiState

@Preview(showBackground = true, name = "Subject List Item - Recent")
@Composable
private fun PreviewSubjectListItemRecent() {
    TikTokAppTheme {
        SubjectListItem(
            subject = SubjectEntity(
                id = "1",
                user = "Jean Dupont",
                title = "Discussion sur le projet",
                timestamp = System.currentTimeMillis() - 300000 // Il y a 5 minutes
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Subject List Item - Old")
@Composable
private fun PreviewSubjectListItemOld() {
    TikTokAppTheme {
        SubjectListItem(
            subject = SubjectEntity(
                id = "2",
                user = "Marie Martin",
                title = "Réunion d'équipe - Notes importantes à ne pas oublier",
                timestamp = System.currentTimeMillis() - 86400000 * 3 // Il y a 3 jours
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Subjects List - Empty")
@Composable
private fun PreviewSubjectsListEmpty() {
    TikTokAppTheme {
        SubjectsListContent(
            uiState = SubjectUiState(
                subjects = emptyList(),
                isLoading = false,
                error = null
            ),
            onSubjectClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Subjects List - Loading")
@Composable
private fun PreviewSubjectsListLoading() {
    TikTokAppTheme {
        SubjectsListContent(
            uiState = SubjectUiState(
                subjects = emptyList(),
                isLoading = true,
                error = null
            ),
            onSubjectClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Subjects List - Error")
@Composable
private fun PreviewSubjectsListError() {
    TikTokAppTheme {
        SubjectsListContent(
            uiState = SubjectUiState(
                subjects = emptyList(),
                isLoading = false,
                error = "Impossible de charger les messages"
            ),
            onSubjectClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Subjects List - With Data")
@Composable
private fun PreviewSubjectsListWithData() {
    TikTokAppTheme {
        SubjectsListContent(
            uiState = SubjectUiState(
                subjects = listOf(
                    SubjectEntity(
                        id = "1",
                        user = "Jean Dupont",
                        title = "Discussion sur le projet",
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
                    ),
                    SubjectEntity(
                        id = "4",
                        user = "Sophie Bernard",
                        title = "Feedback sur la dernière version",
                        timestamp = System.currentTimeMillis() - 86400000 * 2
                    ),
                    SubjectEntity(
                        id = "5",
                        user = "Thomas Petit",
                        title = "Planification sprint",
                        timestamp = System.currentTimeMillis() - 86400000 * 5
                    )
                ),
                isLoading = false,
                error = null
            ),
            onSubjectClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Create Subject Dialog")
@Composable
private fun PreviewCreateSubjectDialog() {
    TikTokAppTheme {
        CreateSubjectDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}
