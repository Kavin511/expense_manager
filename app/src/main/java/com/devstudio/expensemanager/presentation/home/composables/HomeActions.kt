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
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.devstudio.core_data.repository.TransactionDataBackupWorker
import com.devstudio.core_model.models.BackupStatus
import com.devstudio.core_model.models.ExpressWalletAppState
import com.devstudio.core_model.models.Status.SUCCESS
import com.devstudio.expensemanager.presentation.home.viewmodel.HomeActionsViewModel
import com.devstudio.expensemanager.presentation.home.viewmodel.HomeActionsViewModel.Companion.SHARE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun HomeActions(navController: NavHostController, snackBarHostState: SnackbarHostState) {
    val context = LocalContext.current
    var readPermissionGranted = false
    var writePermissionGranted = false
    val permissionList = mutableListOf<String>()
    val homeActionsViewModel = hiltViewModel<HomeActionsViewModel>()

    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { it ->
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
                    backUpTransactions(homeActionsViewModel, snackBarHostState, context)
                }
            }
        }
        if (permissions.entries.any { !it.value }) {
            Toast.makeText(
                context, "Permissions required to start backup", Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)

        }
    }

    fun isSdk29Up() = Build.VERSION.SDK_INT > Build.VERSION_CODES.Q

    fun isSdk33Up() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun isReadPermissionRequired(context: Context) = ContextCompat.checkSelfPermission(
        context, READ_EXTERNAL_STORAGE
    ) == PERMISSION_GRANTED || isSdk33Up()

    fun isWritePermissionRequired(context: Context) = ContextCompat.checkSelfPermission(
        context, WRITE_EXTERNAL_STORAGE
    ) == PERMISSION_GRANTED || isSdk29Up()

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

    IconButton(onClick = {
        if (checkPermissionToStartBackup(context)) {
            backUpTransactions(homeActionsViewModel, snackBarHostState, context)
        }
    }) {
        Icon(Icons.Rounded.Backup, BACKUP)
    }
    IconButton(onClick = {
        navController.navigate(ExpressWalletAppState.HomeScreen.CategoryScreen.route) {
            launchSingleTop = true
        }
    }) {
        Icon(Icons.Rounded.Category, "Category")
    }
}

private fun backUpTransactions(
    homeActionsViewModel: HomeActionsViewModel, snackBarHostState: SnackbarHostState, context: Context
) {
    homeActionsViewModel.exportTransactions(true) {
        if (it.status == SUCCESS) {
            showBackUpResultAlert(it, snackBarHostState, context)
        } else {
            Toast.makeText(
                context, it.message ?: "", Toast.LENGTH_SHORT
            ).show()
        }
    }
}

private fun showBackUpResultAlert(backStatus: BackupStatus, snackBarHostState: SnackbarHostState, context: Context) {
    CoroutineScope(Dispatchers.Main).launch {
        val snackBarResult = snackBarHostState.showSnackbar(
            actionLabel = SHARE,
            duration = SnackbarDuration.Short,
            message = backStatus.message,
            withDismissAction = true,
        )
        when (snackBarResult) {
            SnackbarResult.Dismissed -> {

            }

            SnackbarResult.ActionPerformed -> {
                createIntentToShareTransactions(context)
            }
        }
    }
}

fun createIntentToShareTransactions(context: Context) {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.type = HomeActionsViewModel.CSV_INTENT_TYPE
    intent.putExtra(
        Intent.EXTRA_STREAM, FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            TransactionDataBackupWorker.getFileToStoreTransactions(context)
        )
    )
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    intent.putExtra(Intent.EXTRA_SUBJECT, "Transactions export")
    context.startActivity(Intent.createChooser(intent, "Share Transactions"))
}

const val BACKUP = "Backup"