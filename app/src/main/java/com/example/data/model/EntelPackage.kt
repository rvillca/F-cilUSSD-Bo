package com.example.data.model

data class EntelPackage(
    val id: String,
    val title: String,
    val megasLabel: String,
    val megasAmount: Int,
    val priceBs: Double,
    val validityLabel: String,
    val validityHours: Int,
    val ussdCode: String,
    val category: PackageCategory,
    val isFeatured: Boolean = false,
    val description: String = ""
)

enum class PackageCategory(val label: String) {
    FEATURED("Destacado"),
    DAILY("Diarios"),
    WEEKLY("Semanales"),
    MONTHLY("Mensuales"),
    CUSTOM("Personalizados")
}

object EntelPackageCatalog {
    val STAR_PACKAGE = EntelPackage(
        id = "direct_200mb_2bs",
        title = "200 MB Diario",
        megasLabel = "200 MB",
        megasAmount = 200,
        priceBs = 2.0,
        validityLabel = "24 Horas",
        validityHours = 24,
        ussdCode = "*10*1*1*1*1#",
        category = PackageCategory.FEATURED,
        isFeatured = true,
        description = "Compra directa instantánea por 2 Bs válida por 24 horas"
    )

    val DEFAULT_PACKAGES: List<EntelPackage> = listOf(
        STAR_PACKAGE,
        EntelPackage(
            id = "pack_500mb_5bs",
            title = "500 MB Diario",
            megasLabel = "500 MB",
            megasAmount = 500,
            priceBs = 5.0,
            validityLabel = "24 Horas",
            validityHours = 24,
            ussdCode = "*10*1*1*2#",
            category = PackageCategory.DAILY,
            description = "Paquete diario por 5 Bs"
        ),
        EntelPackage(
            id = "pack_1000mb_10bs",
            title = "1000 MB (1 GB) Diario",
            megasLabel = "1 GB",
            megasAmount = 1000,
            priceBs = 10.0,
            validityLabel = "24 Horas",
            validityHours = 24,
            ussdCode = "*10*1*1*3#",
            category = PackageCategory.DAILY,
            description = "1 Gigabyte de datos por 10 Bs"
        ),
        EntelPackage(
            id = "pack_nocturno_4bs",
            title = "Ilimitado Nocturno",
            megasLabel = "Ilimitado",
            megasAmount = 5000,
            priceBs = 4.0,
            validityLabel = "00:00 a 07:00",
            validityHours = 7,
            ussdCode = "*10*1*1*4#",
            category = PackageCategory.DAILY,
            description = "Navegación nocturna ilimitada por 4 Bs"
        ),
        EntelPackage(
            id = "pack_1500mb_15bs",
            title = "1.5 GB Semanal",
            megasLabel = "1.5 GB",
            megasAmount = 1500,
            priceBs = 15.0,
            validityLabel = "7 Días",
            validityHours = 168,
            ussdCode = "*10*1*2*1#",
            category = PackageCategory.WEEKLY,
            description = "1500 MB válidos por 7 días por 15 Bs"
        ),
        EntelPackage(
            id = "pack_3000mb_25bs",
            title = "3 GB Semanal",
            megasLabel = "3 GB",
            megasAmount = 3000,
            priceBs = 25.0,
            validityLabel = "7 Días",
            validityHours = 168,
            ussdCode = "*10*1*2*2#",
            category = PackageCategory.WEEKLY,
            description = "3000 MB válidos por 7 días por 25 Bs"
        ),
        EntelPackage(
            id = "pack_6000mb_50bs",
            title = "6 GB Mensual",
            megasLabel = "6 GB",
            megasAmount = 6000,
            priceBs = 50.0,
            validityLabel = "30 Días",
            validityHours = 720,
            ussdCode = "*10*1*3*1#",
            category = PackageCategory.MONTHLY,
            description = "6000 MB válidos por 30 días por 50 Bs"
        ),
        EntelPackage(
            id = "pack_12000mb_100bs",
            title = "12 GB Mensual",
            megasLabel = "12 GB",
            megasAmount = 12000,
            priceBs = 100.0,
            validityLabel = "30 Días",
            validityHours = 720,
            ussdCode = "*10*1*3*2#",
            category = PackageCategory.MONTHLY,
            description = "12000 MB válidos por 30 días por 100 Bs"
        )
    )
}
