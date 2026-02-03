package de.nif.utils.permissions

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.nif.utils.permissions.ui.theme.UtilsTheme

class PermissionsActivity : ComponentActivity() {

    lateinit var permissionManager: PermissionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(
            this,

            object : PermissionManager.PermissionCallback {
                override fun onPermissionResult(
                    permission: String,
                    granted: Boolean
                ) {
                    when (granted) {
                        true -> println("CB Permission $permission granted, do something with it...")
                        false -> {
                            println("CB Permission $permission permanently declined!")
                        }
                    }
                }

            })

        enableEdgeToEdge()
        setContent {
            UtilsTheme {


                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "${Manifest.permission.POST_NOTIFICATIONS}\n${Manifest.permission.RECORD_AUDIO}\n${Manifest.permission.CAMERA}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        permissionManager.requestPermissions(
                            listOf(
                                Manifest.permission.POST_NOTIFICATIONS,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.CAMERA
                            )
                        )
                    })
                    { Text(text = "Ask For Permissions") }

                    for (pr in permissionManager.declined) {

                        PermissionDialog(
                            rationaleText = "Permission ${pr.permission} is needed because ...",
                            isDeniedPermanently = pr.isPermanentlyDeclined,
                            onDismiss = {
                                permissionManager.removeDeclined(pr.permission)
                            },
                            onOkClick = {
                                permissionManager.removeDeclined(pr.permission)
                                permissionManager.launchPermissionRequest(pr.permission)
                            },
                            onGoToAppSettingsClick = {
                                permissionManager.removeDeclined(pr.permission)

                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)

                            }

                        )
                    }


                }
            }
        }
    }


}

