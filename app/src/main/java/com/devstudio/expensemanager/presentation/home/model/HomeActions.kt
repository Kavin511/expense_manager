import HomeActions.BACKUP
import HomeActions.IMPORT_CSV

enum class HomeActions {
    BACKUP, IMPORT_CSV,
}

fun HomeActions.getActionName() = when (this) {
    BACKUP -> "Backup"
    IMPORT_CSV -> "Import CSV"
}