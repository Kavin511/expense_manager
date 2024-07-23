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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.devstudio.data.repository.TransactionDataBackupWorker
import com.devstudio.expensemanager.presentation.home.viewmodel.HomeActionsViewModel
import com.devstudio.expensemanager.presentation.home.viewmodel.HomeActionsViewModel.Companion.SHARE
import com.devstudio.model.models.BackupStatus
import com.devstudio.model.models.Status.SUCCESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeActions(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    event: (String) -> Unit
) {
    val context = LocalContext.current
    var readPermissionGranted = false
    var writePermissionGranted = false
    val permissionList = mutableListOf<String>()
    val homeActionsViewModel = hiltViewModel<HomeActionsViewModel>()

    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
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
                context,
                "Permissions required to start backup",
                Toast.LENGTH_SHORT,
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
        context,
        READ_EXTERNAL_STORAGE,
    ) == PERMISSION_GRANTED || isSdk33Up()

    fun isWritePermissionRequired(context: Context) = ContextCompat.checkSelfPermission(
        context,
        WRITE_EXTERNAL_STORAGE,
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
    ModalBottomSheet(
        onDismissRequest = {
            event.invoke("")
        }, modifier = Modifier
            .navigationBarsPadding()
            .padding(bottom = 32.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
        ) {
            RowWithImage(RowWithImageData(IMPORT_CSV, Icons.Rounded.Upload), onClick = {
                event.invoke(IMPORT_CSV)
            })
            RowWithImage(RowWithImageData(BACKUP, Icons.Rounded.Backup), onClick = {
                event.invoke(BACKUP)
                if (checkPermissionToStartBackup(context)) {
                    backUpTransactions(homeActionsViewModel, snackBarHostState, context)
                }
            })
        }
    }
}

private fun backUpTransactions(
    homeActionsViewModel: HomeActionsViewModel,
    snackBarHostState: SnackbarHostState,
    context: Context,
) {
    homeActionsViewModel.exportTransactions(true) {
        if (it.status == SUCCESS) {
            showBackUpResultAlert(it, snackBarHostState, context)
        } else {
            Toast.makeText(
                context,
                it.message ?: "",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}

private fun showBackUpResultAlert(
    backStatus: BackupStatus, snackBarHostState: SnackbarHostState, context: Context
) {
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

fun openFileLocation(context: Context) {
    val uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        TransactionDataBackupWorker.getFileToStoreTransactions(context),
    )
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, "*/*")
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    context.startActivity(intent)
}

fun createIntentToShareTransactions(context: Context) {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.type = HomeActionsViewModel.CSV_INTENT_TYPE
    intent.putExtra(
        Intent.EXTRA_STREAM,
        FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            TransactionDataBackupWorker.getFileToStoreTransactions(context),
        ),
    )
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    intent.putExtra(Intent.EXTRA_SUBJECT, "Transactions export")
    context.startActivity(Intent.createChooser(intent, "Share Transactions"))
}

const val BACKUP = "Backup"
const val IMPORT_CSV = "Import CSV"

data class RowWithImageData(val text: String, val iconResource: ImageVector)

@Composable
fun RowWithImage(data: RowWithImageData, onClick: () -> Unit) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .clickable(interactionSource = remember {
            MutableInteractionSource()
        }, indication = rememberRipple()) {
            onClick()
        }
        .padding(
            vertical = 12.dp, horizontal = 16.dp
        )) {
        val (selectedFilterTextView) = createRefs()
        Row(modifier = Modifier.constrainAs(selectedFilterTextView) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }) {
            val icon = data.iconResource
            Icon(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .width(20.dp)
                    .align(Alignment.CenterVertically),
                imageVector = icon,
                contentDescription = null
            )
            Text(
                text = data.text,
                fontSize = 16.sp,
            )
        }
    }
}