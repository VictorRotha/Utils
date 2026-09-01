package de.nif.utils.lazycolumndrag

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    title : String = "",
    isDragging: Boolean = false
) {
    val ele by animateDpAsState( if (isDragging) 4.dp else 1.dp)
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = ele,
        )

    ) {

        Text(text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding( 4.dp)
        )


    }



}