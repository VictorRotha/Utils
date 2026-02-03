package de.nif.utils.permissions

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission

class PermissionManager(
    val activity: ComponentActivity,
    val callback: PermissionCallback
) {

    var launcher : ActivityResultLauncher<Array<String>>? = null

    data class PermissionResult(
        val permission: String,
        val isPermanentlyDeclined: Boolean,
    )

    interface PermissionCallback {
        fun onPermissionResult(
            permission: String,
            granted: Boolean
        )
    }

    private var _declined by mutableStateOf(emptyList<PermissionResult>())
    val declined
        get() = _declined


    init {
            launcher = activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->

                var results = declined.toList()
                permissions.forEach { (permission, isGranted) ->

                    when {
                        isGranted -> {
                            callback.onPermissionResult(permission, true)
                        }

                        shouldShowRequestPermissionRationale(activity, permission) -> {
                            results = results.filter { it.permission != permission }.toMutableList()
                                .also {
                                    it.add(PermissionResult(permission, false))
                                }
                        }

                        else -> {
                            results = results.filter { it.permission != permission }.toMutableList()
                                .also {
                                    it.add(PermissionResult(permission, true))
                                }
                            callback.onPermissionResult(permission, false)
                        }
                    }
                }

                _declined = results
            }


    }

    fun launchPermissionRequest(permission: String){
        launcher?.launch(arrayOf(permission))
    }

    fun removeDeclined(permission: String) {
        _declined = declined.filter { it.permission != permission }
    }

    fun requestPermissions(permissions: List<String>){

        val rationales = mutableListOf<PermissionResult>()
        val requests = mutableListOf<String>()

        for (permission in permissions) {
            when {
                (checkSelfPermission(
                    activity,
                    permission
                ) == PackageManager.PERMISSION_GRANTED) -> {
                    callback.onPermissionResult(permission, true)
                }

                (shouldShowRequestPermissionRationale(activity, permission)) -> {
                    rationales.add(PermissionResult(permission, false))
                }

                else -> {
                    requests.add(permission)
                }
            }
        }

        _declined = rationales

        if (requests.isNotEmpty()) {
            launcher?.launch(requests.toTypedArray())
        }

    }

}