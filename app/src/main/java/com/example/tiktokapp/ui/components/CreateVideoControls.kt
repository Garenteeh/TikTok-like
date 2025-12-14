package com.example.tiktokapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tiktokapp.R
import com.example.tiktokapp.ui.components.signup.BaseTextField

/**
 * Controls used on the CreateVideo screen: title field, pick/record buttons and save button.
 * This is UI-only and does not hold any ViewModel dependency.
 */
@Composable
fun CreateVideoControls(
    title: String,
    onTitleChange: (String) -> Unit,
    onPickClick: () -> Unit,
    onRecordClick: () -> Unit,
    onSaveClick: () -> Unit,
    isSaving: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BaseTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "Titre",
            isError = error == null,
            errorMessage = error ?: "",
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.titre),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        )



        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onPickClick) {
                Text("Choisir une vidéo")
            }

            Button(onClick = onRecordClick) {
                Text("Enregistrer (caméra)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isSaving) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sauvegarder")
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it)
        }
    }
}