package de.nif.utils.lazycolumndrag

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex


@Composable
fun LazyItemScope.DraggableItem(
    modifier: Modifier = Modifier,
    dragDropState: DragDropState,
    index: Int,
    content: @Composable (isDragging: Boolean) -> Unit
) {

    val isDragging = dragDropState.dragItemIndex == index
    val dragModifier =
        if (isDragging)
            Modifier
                .zIndex(1f)
                .graphicsLayer {
                    translationY = dragDropState.dragItemOffset
                }
        else if (dragDropState.prevDragItemIndex == index){
            Modifier
                .zIndex(1f)
                .graphicsLayer {
                    translationY = dragDropState.prevItemOffset.value
                }
        } else {
            Modifier.animateItem()
        }

    Column(modifier = modifier.then(dragModifier)) {
        content(isDragging)
    }

}

