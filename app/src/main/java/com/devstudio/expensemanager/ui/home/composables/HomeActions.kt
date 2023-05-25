import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.expensemanager.viewmodel.HomeViewModel


@Composable
fun HomeActions() {
    val context = LocalContext.current
    var readPermissionGranted = false
    var writePermissionGranted = false
    val permissionList = mutableListOf<String>()
    val homeViewModel = hiltViewModel<HomeViewModel>()

    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    )
    { permissions ->
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (isGranted) {
                if (permissionName == WRITE_EXTERNAL_STORAGE) {
                    writePermissionGranted = true
                }
                if (permissionName == READ_EXTERNAL_STORAGE) {
                    readPermissionGranted = true
                }
                if (readPermissionGranted && writePermissionGranted) {
                    homeViewModel.exportTransactions()
                }
            }
        }
        if (permissions.entries.any { !it.value }) {
            Toast.makeText(
                context,
                "Permissions required to start backup",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)

        }
    }

    fun isSdk29Up() = Build.VERSION.SDK_INT > Build.VERSION_CODES.Q

    fun isSdk33Up() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun isReadPermissionRequired(context: Context) = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED || isSdk33Up()

    fun isWritePermissionRequired(context: Context) = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED || isSdk29Up()

    fun checkPermissionToStartBackup(context: Context): Boolean {
        readPermissionGranted = isReadPermissionRequired(context)
        writePermissionGranted = isWritePermissionRequired(context)
        if (!writePermissionGranted) {
            permissionList.add(WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionList.add(READ_EXTERNAL_STORAGE)
        }
        return if (permissionList.isNotEmpty()) {
            activityResultLauncher.launch(permissionList.toTypedArray())
            false
        } else {
            true
        }
    }

    val outputWorkInfo = homeViewModel.outputWorkInformation.observeAsState()
    (outputWorkInfo.value).let {
        if (it?.isNotEmpty() == true) {
            if (it[0].state.isFinished) {
                Toast.makeText(
                    LocalContext.current,
                    it[0].outputData.getString("is_success"),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    IconButton(onClick = {
        if (checkPermissionToStartBackup(context)) {
            homeViewModel.exportTransactions()
        }
    }) {
        Icon(Icons.Rounded.Backup, "Backup")
    }
}