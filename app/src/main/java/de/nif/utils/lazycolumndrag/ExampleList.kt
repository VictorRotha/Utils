package de.nif.utils.lazycolumndrag

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun ExampleList(
    modifier: Modifier = Modifier,
    data: List<String>,
    onMove: (Int, Int) -> Unit,
    onDragEnd: (Int, Int) -> Unit
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier.padding(8.dp),
            text = "Lazy Column with Drag and Drop",
            style = MaterialTheme.typography.titleMedium
        )

        var useDragHandle by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = useDragHandle,
                onCheckedChange = { useDragHandle = !useDragHandle })

            Spacer(modifier = Modifier.padding(4.dp))

            Text(text = "Use Drag Handle")
        }

        Spacer(modifier = Modifier.padding(8.dp))

        val listState = rememberLazyListState()
        val dragDropState = rememberDragDropState(
            listState = listState,
            onMove = { from, to -> onMove(from, to) },
            onDragEnd = { from, to -> onDragEnd(from, to) },

            )

        if (useDragHandle) {

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()

            ) {
                //providing a unique key for each item instead of just the list position is necessary
                //to make the dragging work properly even for entries with the same content AND animates
                //data changes like removing/adding items
                items(data.size, key = { data[it] }) {
                    DraggableItem(modifier = Modifier, dragDropState = dragDropState, index = it)
                    { isDragging ->

                        ListItemWithDragHandle(
                            modifier = Modifier,
                            dragDropState = dragDropState,
                            key = data[it],
                            title = data[it],
                            isDragging = isDragging
                        )
                    }
                }

            }
        } else {
            //drag after long press
            LazyColumn(
                state = listState,
                modifier = Modifier.pointerInput(key1 = listState) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            dragDropState.onDragStart(it)
                        },
                        onDrag = { change, offset ->
                            change.consume()
                            dragDropState.onDrag(offset)
                        },
                        onDragEnd = {
                            dragDropState.onDragInterrupted()
                        },
                        onDragCancel = {
                            dragDropState.onDragInterrupted()
                        }
                    )
                }

            ) {

                items(data.size, key = { it }) {
                    DraggableItem(modifier = Modifier, dragDropState = dragDropState, index = it)
                    { isDragging ->
                        ListItem(title = data[it], isDragging = isDragging)
                    }

                }

            }


        }


    }


}