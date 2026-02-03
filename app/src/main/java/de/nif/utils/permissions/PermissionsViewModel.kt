package de.nif.utils.permissions

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.sourceInformation
import androidx.lifecycle.ViewModel

class PermissionsViewModel() : ViewModel() {


    val dialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        println("dismissDialog")
    }

    fun onPermissionResult(
        permission: String,
        granted: Boolean
    ) {

        println("PermissionResult : $permission : $granted")




    }
}