package de.nif.utils.lazycolumndrag

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


@Composable
fun rememberDragDropState(
    listState: LazyListState,
    onMove: (Int, Int) -> Unit,
    onDragEnd: (Int, Int) -> Unit,
): DragDropState {
    val scope = rememberCoroutineScope()
    val state = remember(listState) {
        DragDropState(listState, scope, onMove, onDragEnd)
    }
    LaunchedEffect(listState) {
        state.scrollChannel.receiveAsFlow().collect {
            listState.scrollBy(it)
        }
    }

    return state
}

class DragDropState(
    val state: LazyListState,
    val scope: CoroutineScope,
    val onMove: (Int, Int) -> Unit,
    val onDragged: (Int, Int) -> Unit,
) {

    private val logTag = "DragDropState"

    val scrollChannel: Channel<Float> = Channel()

    var dragItemIndex by mutableStateOf<Int?>(null)

    private var dragItemInitialOffset by mutableIntStateOf(0)
    private var dragItemInitialIndex by mutableStateOf<Int?>(null)

    //offset relative to the original position of the item
    val dragItemOffset: Float
        get() = dragItemLayoutInfo?.let {
            dragItemInitialOffset + dragItemDelta - it.offset
        } ?: 0f

    // dragItemLayoutInfo.offset stores the original offset of the item
    private val dragItemLayoutInfo: LazyListItemInfo?
        get() = state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == dragItemIndex }

    private var dragItemDelta by mutableFloatStateOf(0f)

    //index to which the item is being dragged at drag end
    var prevDragItemIndex by mutableStateOf<Int?>(null)
    val prevItemOffset = Animatable(0f)

    fun onDragStart(offset: Offset, index: Int = -1, key: String? = null) {

        if (key != null) {
            // calculate offset from key
            Log.d(logTag, "onDragStart from key: $key")
            state.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                item.key == key
            }?.also {
                dragItemInitialOffset = it.offset
                dragItemIndex = it.index
                dragItemInitialIndex = it.index
            }

        }

        else if (index >= 0)
        // calculate offset from index
            state.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                item.index == index
            }?.also {
                dragItemInitialOffset = it.offset
                dragItemIndex = index
                dragItemInitialIndex = index
            }
        else
        //calculate index from offset
            state.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                offset.y.toInt() in item.offset..item.offset + item.size
            }?.also {
                dragItemIndex = it.index
                dragItemInitialOffset = it.offset
                dragItemInitialIndex = it.index
            }

    }

    fun onDragInterrupted() {

        if (dragItemIndex != null) {

            prevDragItemIndex = dragItemIndex
            onDragged(dragItemInitialIndex!!, prevDragItemIndex!!)

            val startOffset = dragItemOffset
            scope.launch {
                prevItemOffset.snapTo(startOffset)
                prevItemOffset.animateTo(
                    0f,
                    spring(stiffness = Spring.StiffnessMediumLow, visibilityThreshold = 1f),
                )
                prevDragItemIndex = null
            }
        }

        dragItemDelta = 0f
        dragItemIndex = null
        dragItemInitialOffset = 0

    }

    fun onDrag(offset: Offset) {

        dragItemDelta += offset.y

        val dragItem = dragItemLayoutInfo ?: return

        val midOffset = dragItem.offset + dragItemOffset + dragItem.size / 2f
        val endOffset = dragItem.offset + dragItemOffset + dragItem.size
        val startOffset = dragItem.offset + dragItemOffset


        val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
            //smaller overlap range prevents larger items from jumping around
            item.index != dragItem.index && midOffset.toInt() in (item.offset + item.size / 5)..(item.offset + (item.size * .8f).toInt())
        }

        if (targetItem != null) {

            if (dragItem.index == state.firstVisibleItemIndex || targetItem.index == state.firstVisibleItemIndex) {
                state.requestScrollToItem(
                    state.firstVisibleItemIndex,
                    state.firstVisibleItemScrollOffset
                )
            }

            onMove(dragItem.index, targetItem.index)

            dragItemIndex = targetItem.index


        } else {
            val overscroll =
                when {
                    (dragItemDelta > 0) ->
                        (endOffset - state.layoutInfo.viewportEndOffset).coerceAtLeast(0f)

                    (dragItemDelta < 0) ->
                        (startOffset - state.layoutInfo.viewportStartOffset).coerceAtMost(0f)

                    else -> 0f
                }
            if (overscroll != 0f) {
                scrollChannel.trySend(overscroll)
            }


        }
    }


}