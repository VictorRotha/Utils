package de.nif.utils.lazycolumndrag

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun ListItemWithDragHandle(
    modifier: Modifier = Modifier,
    dragDropState: DragDropState,
    key: String,
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

        Row(modifier = Modifier.fillMaxWidth()) {

            Icon(
                modifier = Modifier
                    .pointerInput(key1 = dragDropState) {
                        detectDragGestures(
                            onDragStart = {
                                   dragDropState.onDragStart(offset = it, key = key)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragDropState.onDrag(dragAmount)
                            },
                            onDragEnd = { dragDropState.onDragInterrupted() },
                            onDragCancel = { dragDropState.onDragInterrupted() }
                        )
                    }
                    .padding(end = 8.dp),
                imageVector = Icons.Default.DragHandle,
                contentDescription = "drag handle",
            )

            Text(text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding( 4.dp)
            )
        }




    }



}