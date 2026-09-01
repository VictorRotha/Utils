package de.nif.utils.lazycolumndrag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.nif.utils.lazycolumndrag.ui.theme.UtilsTheme

class DragDropExampleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            UtilsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val exampleData = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10")
                    var data by remember { mutableStateOf(exampleData) }

                    ExampleList(
                        modifier = Modifier.padding(innerPadding),
                        data = data,
                        onMove = {from, to ->
                            //move items in list
                            val currentData = data.toMutableList()
                            val item = currentData.removeAt(from)
                            currentData.add(to, item)
                            data = currentData

                        },
                        onDragEnd = { from, to ->
                            //i.e. persist data to backend
                        }

                    )
                }
            }
        }
    }
}

