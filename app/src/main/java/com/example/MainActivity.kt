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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EntelPackage
import com.example.data.model.EntelPackageCatalog
import com.example.ui.MainViewModel
import com.example.ui.dialogs.AboutLegalDialog
import com.example.ui.dialogs.AddCustomPackageDialog
import com.example.ui.dialogs.AdvancedSettingsDialog
import com.example.ui.dialogs.ConfirmPurchaseDialog
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.GooglePastelBlue
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

                // Tap counter on wallet icon to activate developer mode
                var secretTapCount by remember { mutableIntStateOf(0) }
                var lastTapTimestamp by remember { mutableLongStateOf(0L) }

                // Pending package to dial after permission request
                var pendingPackageToDial by remember { mutableStateOf<EntelPackage?>(null) }

                // Multiple runtime permissions launcher for CALL_PHONE and READ_PHONE_STATE
                val permissionsLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) {
                    viewModel.refreshSimStatus(context)
                    pendingPackageToDial?.let { pkg ->
                        viewModel.buyPackage(context, pkg)
                        pendingPackageToDial = null
                    }
                }

                LaunchedEffect(Unit) {
                    viewModel.refreshSimStatus(context)
                }

                fun requestAndExecute(pkg: EntelPackage) {
                    if (UssdHelper.hasCallPermission(context)) {
                        viewModel.buyPackage(context, pkg)
                    } else {
                        pendingPackageToDial = pkg
                        permissionsLauncher.launch(
                            arrayOf(
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.READ_PHONE_STATE
                            )
                        )
                    }
                }

                // Collect state from ViewModel
                val operatorStatus by viewModel.operatorStatus.collectAsStateWithLifecycle()
                val targetEntelSim by viewModel.targetEntelSim.collectAsStateWithLifecycle()
                val isAdvancedMode by viewModel.isAdvancedMode.collectAsStateWithLifecycle()
                val isSettingsDialogOpen by viewModel.isAdvancedSettingsDialogOpen.collectAsStateWithLifecycle()
                val isAboutDialogOpen by viewModel.isAboutDialogOpen.collectAsStateWithLifecycle()
                val latestPurchase by viewModel.latestPurchase.collectAsStateWithLifecycle()
                val allPurchases by viewModel.allPurchases.collectAsStateWithLifecycle()
                val totalSpentBs by viewModel.totalSpentBs.collectAsStateWithLifecycle()
                val currentTimestamp by viewModel.currentTimeMillis.collectAsStateWithLifecycle()
                val packagesList by viewModel.filteredPackages.collectAsStateWithLifecycle()
                val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val confirmPackage by viewModel.confirmPackageDialog.collectAsStateWithLifecycle()
                val isAddCustomOpen by viewModel.isAddCustomDialogOpen.collectAsStateWithLifecycle()

                fun handleWalletClick() {
                    val now = System.currentTimeMillis()
                    if (now - lastTapTimestamp < 1500) {
                        secretTapCount++
                        if (secretTapCount in 2..4) {
                            val remaining = 5 - secretTapCount
                            Toast.makeText(context, "Modo avanzado: faltan $remaining toques", Toast.LENGTH_SHORT).show()
                        } else if (secretTapCount >= 5) {
                            val enabled = viewModel.toggleAdvancedMode()
                            val msg = if (enabled) "¡Modo desarrollador activado! Pestañas visibles." else "Modo simple para adulto mayor activado."
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            secretTapCount = 0
                        }
                    } else {
                        secretTapCount = 1
                        // Normal behavior: Dial balance using Entel SIM
                        UssdHelper.executeUssd(context, "*105#", targetSim = targetEntelSim)
                    }
                    lastTapTimestamp = now
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "FácilUSSD Bo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { viewModel.openAboutDialog() }) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Información",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            actions = {
                                // If advanced mode is on, show indicator pill
                                if (isAdvancedMode) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(GooglePastelBlue.copy(alpha = 0.2f))
                                            .clickable { viewModel.openAdvancedSettingsDialog() }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Avanzado",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                // Quick balance check button (5 taps activates developer mode)
                                IconButton(
                                    onClick = { handleWalletClick() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Consultar Saldo",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Information & Compliance / About dialog button
                                IconButton(
                                    onClick = { viewModel.openAboutDialog() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Acerca de y Aviso Legal",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Developer / Advanced Settings
                                IconButton(
                                    onClick = { viewModel.openAdvancedSettingsDialog() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Configuración",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    // BOTTOM NAVIGATION BAR:
                    // Hidden completely in "Modo Simple" (default for senior adults)
                    // Visible when "Modo Avanzado" is toggled
                    bottomBar = {
                        if (isAdvancedMode) {
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
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (!isAdvancedMode || selectedTab == 0) {
                            HomeScreen(
                                operatorStatus = operatorStatus,
                                latestPurchase = latestPurchase,
                                currentTimestamp = currentTimestamp,
                                onBuyPackage = { pkg ->
                                    requestAndExecute(pkg)
                                },
                                onCheckBalance = {
                                    UssdHelper.executeUssd(context, "*105#", targetSim = targetEntelSim)
                                },
                                onOpenAboutDialog = {
                                    viewModel.openAboutDialog()
                                }
                            )
                        } else if (selectedTab == 1) {
                            CatalogScreen(
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
                        } else if (selectedTab == 2) {
                            HistoryScreen(
                                purchases = allPurchases,
                                totalSpentBs = totalSpentBs,
                                currentTimestamp = currentTimestamp,
                                onDeletePurchase = viewModel::deletePurchase,
                                onClearHistory = viewModel::clearHistory
                            )
                        }
                    }
                }

                // About & Legal Compliance Dialog (Google Play compliance)
                if (isAboutDialogOpen) {
                    AboutLegalDialog(
                        operatorStatus = operatorStatus,
                        onDismiss = { viewModel.closeAboutDialog() }
                    )
                }

                // Advanced Settings / Debug Dialog
                if (isSettingsDialogOpen) {
                    AdvancedSettingsDialog(
                        isAdvancedMode = isAdvancedMode,
                        onToggleAdvancedMode = {
                            viewModel.toggleAdvancedMode()
                        },
                        onDismiss = { viewModel.closeAdvancedSettingsDialog() }
                    )
                }

                // Purchase confirmation dialog for catalog packages
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
