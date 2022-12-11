package com.devstudio.expensemanager.ui.home.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.devstudio.expensemanager.databinding.ActivityMainBinding
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.ui.home.composables.Home
import com.devstudio.expensemanager.ui.viewmodel.HomeViewModel
import com.devstudioworks.uiComponents.theme.MaterialTheme

class MainActivity : ComponentActivity() {

    private var transactions: List<Transactions> = listOf()
    private lateinit var binding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private var readPermissionGranted=false
    private var writePermissionGranted=false
    private lateinit var permissionLauncher:ActivityResultLauncher<Array<String>>
    var permissionList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Home()
            }
        }
    }

    fun updateOrRequestPermission(){

        var isReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        var isWritePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        writePermissionGranted = isWritePermissionGranted || VERSION.SDK_INT >= VERSION_CODES.Q
        readPermissionGranted  = isReadPermissionGranted
        if(writePermissionGranted){
            permissionList.add(WRITE_EXTERNAL_STORAGE)
        }
        if(readPermissionGranted){
            permissionList.add(READ_EXTERNAL_STORAGE)
        }
        if (permissionList.isNotEmpty()){
            permissionLauncher.launch(permissionList.toTypedArray())
            activityResultLauncher
        }
    }

    private val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
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
            }
        }
    }

}