package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.EntelPackage
import com.example.data.model.EntelPackageCatalog
import com.example.data.model.PackageCategory
import com.example.data.model.PurchaseRecord
import com.example.data.repository.PurchaseRepository
import com.example.util.UssdHelper
import com.example.widget.EntelMegasWidgetProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PurchaseRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PurchaseRepository(database.purchaseDao())
    }

    val allPurchases: StateFlow<List<PurchaseRecord>> = repository.allPurchases
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val latestPurchase: StateFlow<PurchaseRecord?> = repository.latestPurchase
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val totalSpentBs: StateFlow<Double> = repository.totalSpentBs
        .map { it ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // Current time ticker for live countdowns
    private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                _currentTimeMillis.value = System.currentTimeMillis()
                delay(1000)
            }
        }
    }

    private val _customPackages = MutableStateFlow<List<EntelPackage>>(emptyList())
    private val _selectedCategory = MutableStateFlow<PackageCategory?>(null)
    val selectedCategory: StateFlow<PackageCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredPackages: StateFlow<List<EntelPackage>> = combine(
        _customPackages,
        _selectedCategory,
        _searchQuery
    ) { custom, category, query ->
        val fullList = EntelPackageCatalog.DEFAULT_PACKAGES + custom
        fullList.filter { pkg ->
            val matchesCategory = category == null || pkg.category == category
            val matchesQuery = query.isBlank() ||
                    pkg.title.contains(query, ignoreCase = true) ||
                    pkg.megasLabel.contains(query, ignoreCase = true) ||
                    pkg.ussdCode.contains(query, ignoreCase = true) ||
                    pkg.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EntelPackageCatalog.DEFAULT_PACKAGES
    )

    // Dialog states
    private val _confirmPackageDialog = MutableStateFlow<EntelPackage?>(null)
    val confirmPackageDialog: StateFlow<EntelPackage?> = _confirmPackageDialog.asStateFlow()

    private val _isAddCustomDialogOpen = MutableStateFlow(false)
    val isAddCustomDialogOpen: StateFlow<Boolean> = _isAddCustomDialogOpen.asStateFlow()

    fun openConfirmDialog(pkg: EntelPackage) {
        _confirmPackageDialog.value = pkg
    }

    fun closeConfirmDialog() {
        _confirmPackageDialog.value = null
    }

    fun openAddCustomDialog() {
        _isAddCustomDialogOpen.value = true
    }

    fun closeAddCustomDialog() {
        _isAddCustomDialogOpen.value = false
    }

    fun onCategorySelect(category: PackageCategory?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /**
     * Direct purchase for the user's requested 200MB x 2 Bs (24h) package
     * with USSD code *10*1*1*1*1#
     */
    fun buyStarPackage(context: Context) {
        buyPackage(context, EntelPackageCatalog.STAR_PACKAGE)
    }

    /**
     * Executes USSD and records purchase in Room
     */
    fun buyPackage(context: Context, pkg: EntelPackage) {
        if (pkg.priceBs > 0) {
            val record = PurchaseRecord(
                packageName = "${pkg.megasLabel} (${pkg.priceBs.toInt()} Bs)",
                megasAmount = pkg.megasAmount,
                priceBs = pkg.priceBs,
                ussdCode = pkg.ussdCode,
                validityHours = if (pkg.validityHours > 0) pkg.validityHours else 24,
                notes = if (pkg.isFeatured) "Compra Directa Rápida" else "Catálogo Entel"
            )

            viewModelScope.launch {
                repository.insertPurchase(record)
                EntelMegasWidgetProvider.updateAllWidgets(getApplication())
            }
        }

        UssdHelper.executeUssd(context, pkg.ussdCode)
    }

    fun addCustomPackage(title: String, megasAmount: Int, priceBs: Double, hours: Int, ussdCode: String) {
        val newPkg = EntelPackage(
            id = "custom_${System.currentTimeMillis()}",
            title = title,
            megasLabel = if (megasAmount >= 1000) "${megasAmount / 1000} GB" else "$megasAmount MB",
            megasAmount = megasAmount,
            priceBs = priceBs,
            validityLabel = if (hours >= 24) "${hours / 24} Días" else "$hours Horas",
            validityHours = hours,
            ussdCode = if (ussdCode.startsWith("*") && ussdCode.endsWith("#")) ussdCode else "*$ussdCode#",
            category = PackageCategory.CUSTOM,
            description = "Paquete personalizado por el usuario"
        )
        _customPackages.value = _customPackages.value + newPkg
        closeAddCustomDialog()
    }

    fun deletePurchase(record: PurchaseRecord) {
        viewModelScope.launch {
            repository.deletePurchase(record)
            EntelMegasWidgetProvider.updateAllWidgets(getApplication())
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            EntelMegasWidgetProvider.updateAllWidgets(getApplication())
        }
    }
}
