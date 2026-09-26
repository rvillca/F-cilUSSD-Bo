package com.example

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EntelPackage
import com.example.data.model.EntelPackageCatalog
import com.example.ui.MainViewModel
import com.example.ui.dialogs.AddCustomPackageDialog
import com.example.ui.dialogs.ConfirmPurchaseDialog
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.UssdHelper

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                var selectedTab by remember { mutableIntStateOf(0) }

                // Pending package to dial after permission request
                var pendingPackageToDial by remember { androidx.compose.runtime.mutableStateOf<EntelPackage?>(null) }

                // Launcher for CALL_PHONE permission
                val callPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    pendingPackageToDial?.let { pkg ->
                        viewModel.buyPackage(context, pkg)
                        pendingPackageToDial = null
                    }
                }

                fun requestAndExecute(pkg: EntelPackage) {
                    if (UssdHelper.hasCallPermission(context)) {
                        viewModel.buyPackage(context, pkg)
                    } else {
                        pendingPackageToDial = pkg
                        callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                    }
                }

                // Collect state from ViewModel
                val latestPurchase by viewModel.latestPurchase.collectAsStateWithLifecycle()
                val allPurchases by viewModel.allPurchases.collectAsStateWithLifecycle()
                val totalSpentBs by viewModel.totalSpentBs.collectAsStateWithLifecycle()
                val currentTimestamp by viewModel.currentTimeMillis.collectAsStateWithLifecycle()
                val packagesList by viewModel.filteredPackages.collectAsStateWithLifecycle()
                val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val confirmPackage by viewModel.confirmPackageDialog.collectAsStateWithLifecycle()
                val isAddCustomOpen by viewModel.isAddCustomDialogOpen.collectAsStateWithLifecycle()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Megas Entel",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {}) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        UssdHelper.executeUssd(context, "*105#")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Consultar Saldo (*105#)",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Dialpad,
                                        contentDescription = "Inicio"
                                    )
                                },
                                label = { Text("Inicio", fontSize = 12.sp) }
                            )

                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.GridView,
                                        contentDescription = "Paquetes"
                                    )
                                },
                                label = { Text("Paquetes", fontSize = 12.sp) }
                            )

                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "Historial"
                                    )
                                },
                                label = { Text("Historial", fontSize = 12.sp) }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (selectedTab) {
                            0 -> HomeScreen(
                                latestPurchase = latestPurchase,
                                currentTimestamp = currentTimestamp,
                                onBuyStarPackage = {
                                    requestAndExecute(EntelPackageCatalog.STAR_PACKAGE)
                                }
                            )
                            1 -> CatalogScreen(
                                packages = packagesList,
                                selectedCategory = selectedCategory,
                                searchQuery = searchQuery,
                                onCategorySelect = viewModel::onCategorySelect,
                                onSearchQueryChange = viewModel::onSearchQueryChange,
                                onPackageSelect = { pkg ->
                                    viewModel.openConfirmDialog(pkg)
                                },
                                onAddCustomClick = viewModel::openAddCustomDialog
                            )
                            2 -> HistoryScreen(
                                purchases = allPurchases,
                                totalSpentBs = totalSpentBs,
                                currentTimestamp = currentTimestamp,
                                onDeletePurchase = viewModel::deletePurchase,
                                onClearHistory = viewModel::clearHistory
                            )
                        }
                    }
                }

                // Confirmation and creation dialogs
                confirmPackage?.let { pkg ->
                    ConfirmPurchaseDialog(
                        pkg = pkg,
                        onConfirm = {
                            viewModel.closeConfirmDialog()
                            requestAndExecute(pkg)
                        },
                        onDismiss = viewModel::closeConfirmDialog
                    )
                }

                if (isAddCustomOpen) {
                    AddCustomPackageDialog(
                        onAdd = { title, megas, price, hours, ussd ->
                            viewModel.addCustomPackage(title, megas, price, hours, ussd)
                            Toast.makeText(context, "Paquete guardado", Toast.LENGTH_SHORT).show()
                        },
                        onDismiss = viewModel::closeAddCustomDialog
                    )
                }
            }
        }
    }
}
