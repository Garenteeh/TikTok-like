 package com.example.tiktokapp.ui.components.signup

 import androidx.compose.foundation.background
 import androidx.compose.foundation.layout.Arrangement
 import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.Row
 import androidx.compose.foundation.layout.fillMaxWidth
 import androidx.compose.foundation.layout.padding
 import androidx.compose.foundation.shape.RoundedCornerShape
 import androidx.compose.foundation.text.KeyboardOptions
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.DateRange
 import androidx.compose.material3.*
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.text.TextRange
 import androidx.compose.ui.text.input.KeyboardType
 import androidx.compose.ui.text.input.TextFieldValue
 import androidx.compose.ui.text.input.VisualTransformation
 import androidx.compose.ui.tooling.preview.Preview
 import androidx.compose.ui.unit.dp
 import androidx.compose.ui.unit.sp

 @Composable
 fun BirthDateTextField(
     value: String = "",
     onValueChange: (String) -> Unit = {},
     error: String = "",
     isError: Boolean = false
 ) {
     var textFieldValue by remember { mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length))) }

     Column() {

         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .background(
                     color = Color(0xFFF2F2F2),
                     shape = RoundedCornerShape(12.dp)
                 )
                 .padding(horizontal = 12.dp, vertical = 4.dp),
             verticalAlignment = Alignment.CenterVertically
         ) {

//            leadingIcon()
//
//            Spacer(modifier = Modifier.width(10.dp))

             OutlinedTextField(
                 value = textFieldValue,
                 onValueChange = { incoming: TextFieldValue ->
                     val prevText = textFieldValue.text
                     val formatted = formatBirthDate(incoming.text, prevText)

                     val prevDigits = prevText.filter { it.isDigit() }
                     val newDigits = incoming.text.filter { it.isDigit() }

                     // Calculer position curseur souhaitée
                     val desiredCursor = if (newDigits.length > prevDigits.length) {
                         // insertion : placer après le caractère saisi et après les slashes automatiques
                         var pos = newDigits.length
                         if (newDigits.length > 2) pos += 1
                         if (newDigits.length > 4) pos += 1
                         pos.coerceAtMost(formatted.length)
                     } else {
                         // suppression ou édition : essayer de préserver la position relative
                         incoming.selection.start.coerceAtMost(formatted.length)
                     }

                     val newTfv = TextFieldValue(formatted, TextRange(desiredCursor))
                     textFieldValue = newTfv

                     // notifier le parent avec la chaîne formatée
                     onValueChange(formatted)
                 },
                 label = { Text("Date de naissance (JJ/MM/AAAA)") },
                 isError = isError,
                 modifier = Modifier.weight(1f),
                 leadingIcon= { Icon(imageVector = Icons.Default.DateRange, contentDescription = null) },
                 keyboardOptions = KeyboardOptions.Default,
                 visualTransformation = VisualTransformation.None,
                 colors = TextFieldDefaults.colors(
                     focusedContainerColor = Color.Transparent,
                     unfocusedContainerColor = Color.Transparent,
                     disabledContainerColor = Color.Transparent,
                     errorContainerColor = Color.Transparent,
                     focusedIndicatorColor = Color.Transparent,
                     unfocusedIndicatorColor = Color.Transparent,
                     disabledIndicatorColor = Color.Transparent,
                     errorIndicatorColor = Color.Transparent
                 )

             )
         }

         if (isError) {
             Text(
                 text = error,
                 color = MaterialTheme.colorScheme.error,
                 fontSize = 12.sp,
                 modifier = Modifier.padding(start = 8.dp, top = 4.dp)
             )
         }
     }
 }

 fun formatBirthDate(newInput: String, oldInput: String): String {
     var digits = newInput.filter { it.isDigit() }
     if (digits.length > 8) digits = digits.take(8)

     val formatted = when {
         digits.length >= 5 -> digits.substring(0, 2) + "/" +
                 digits.substring(2, 4) + "/" +
                 digits.substring(4)
         digits.length >= 3 -> digits.substring(0, 2) + "/" +
                 digits.substring(2)
         digits.length >= 1 -> digits
         else -> ""
     }

     return formatted
 }

 @Preview(showBackground = true)
 @Composable
 fun BirthDateFieldPreview() {
     var date by remember { mutableStateOf("") }
     BirthDateTextField(
         value = date,
         onValueChange = { date = it }
     )
 }