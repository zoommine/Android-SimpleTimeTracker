package com.example.util.simpletimetracker.feature_settings.viewModel.delegate

import com.example.util.simpletimetracker.core.base.ViewModelDelegate
import com.example.util.simpletimetracker.domain.backup.interactor.AutomaticBackupInteractor
import com.example.util.simpletimetracker.domain.backup.interactor.AutomaticExportInteractor
import com.example.util.simpletimetracker.domain.extension.flip
import com.example.util.simpletimetracker.domain.prefs.interactor.PrefsInteractor
import com.example.util.simpletimetracker.domain.backup.model.BackupOptionsData
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_settings.api.SettingsBlock
import com.example.util.simpletimetracker.feature_settings.interactor.SettingsDataManagementViewDataInteractor
import com.example.util.simpletimetracker.feature_settings.interactor.SettingsOpenDateTimeDialogRouter
import com.example.util.simpletimetracker.feature_settings.interactor.SettingsOptionsUpdateInteractor
import com.example.util.simpletimetracker.feature_settings.mapper.SettingsMapper
import com.example.util.simpletimetracker.feature_settings.model.OptionsContent
import com.example.util.simpletimetracker.feature_settings.model.SettingsDialogTags
import com.example.util.simpletimetracker.navigation.Router
import com.example.util.simpletimetracker.navigation.params.screen.ArchiveParams
import com.example.util.simpletimetracker.navigation.params.screen.BackupOptionsParams
import com.example.util.simpletimetracker.navigation.params.screen.CategoriesParams
import com.example.util.simpletimetracker.navigation.params.screen.DataExportSettingsResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsDataManagementViewModelDelegate @Inject constructor(
    private val router: Router,
    private val settingsDataManagementViewDataInteractor: SettingsDataManagementViewDataInteractor,
    private val settingsFileWorkDelegate: SettingsFileWorkDelegate,
    private val prefsInteractor: PrefsInteractor,
    private val settingsMapper: SettingsMapper,
    private val automaticBackupInteractor: AutomaticBackupInteractor,
    private val automaticExportInteractor: AutomaticExportInteractor,
    private val settingsOptionsUpdateInteractor: SettingsOptionsUpdateInteractor,
    private val settingsOpenDateTimeDialogRouter: SettingsOpenDateTimeDialogRouter,
) : SettingsDelegate, ViewModelDelegate() {

    private var parent: SettingsParent? = null
    private var isCollapsed: Boolean = true

    override fun init(parent: SettingsParent) {
        this.parent = parent
    }

    override suspend fun getViewData(): SettingsDelegate.ViewData {
        return SettingsDelegate.ViewData(
            key = Companion,
            data = settingsDataManagementViewDataInteractor.execute(isCollapsed = isCollapsed),
        )
    }

    override suspend fun getSheetViewData(content: OptionsContent): List<ViewHolderType>? {
        return when (content) {
            OptionsContent.ExportAdvanced -> settingsDataManagementViewDataInteractor.executeAdvanced()
            else -> null
        }
    }

    override fun onBlockClicked(block: SettingsBlock) {
        when (block) {
            SettingsBlock.DataManagementCollapse ->
                onCollapseClick()
            SettingsBlock.Categories ->
                onEditCategoriesClick()
            SettingsBlock.Archive ->
                onArchiveClick()
            SettingsBlock.BackupSave ->
                settingsFileWorkDelegate.onSaveClick(params = BackupOptionsData.Save.Standard)
            SettingsBlock.BackupAutomatic ->
                settingsFileWorkDelegate.onAutomaticBackupClick()
            SettingsBlock.BackupAutomaticTime ->
                onAutoBackupTriggerTimeClicked()
            SettingsBlock.BackupRestore ->
                settingsFileWorkDelegate.onRestoreClick(
                    tag = BACKUP_RESTORE_DIALOG_TAG,
                    params = BackupOptionsData.Restore.Standard,
                )
            SettingsBlock.BackupCustomized ->
                onBackupOptionsClick()
            SettingsBlock.ExportSpreadsheet ->
                settingsFileWorkDelegate.onExportCsvClick(CSV_EXPORT_DIALOG_TAG)
            SettingsBlock.ExportSpreadsheetAutomatic ->
                settingsFileWorkDelegate.onAutomaticExportClick()
            SettingsBlock.ExportSpreadsheetAutomaticTime ->
                onAutoExportTriggerTimeClicked()
            SettingsBlock.ExportSpreadsheetImport ->
                settingsFileWorkDelegate.onImportCsvClick(CSV_IMPORT_ALERT_DIALOG_TAG)
            SettingsBlock.ExportSpreadsheetImportHint ->
                settingsFileWorkDelegate.onImportCsvHelpClick()
            SettingsBlock.ExportIcs -> delegateScope.launch {
                settingsOptionsUpdateInteractor.sendDismiss()
                delay(200)
                settingsFileWorkDelegate.onExportIcsClick(ICS_EXPORT_DIALOG_TAG)
            }
            SettingsBlock.ExportCustomized ->
                onExportCustomizedClick()
            SettingsBlock.ExportTriggerAutoBackup ->
                onTriggerAutoExportClick()
            else -> {
                // Do nothing
            }
        }
    }

    override fun onSpinnerPositionSelected(block: SettingsBlock, position: Int) {
        when (block) {
            SettingsBlock.ExportSpreadsheetDateTimeFormat -> onDateTimeFormatSelected(position)
            else -> {
                // Do nothing
            }
        }
    }

    override fun onDateTimeSet(timestamp: Long, tag: String?) {
        onDateTimeSetDelegate(timestamp, tag)
    }

    override fun onPositiveClick(tag: String?) {
        when (tag) {
            BACKUP_RESTORE_DIALOG_TAG ->
                settingsFileWorkDelegate.onRestoreConfirmed()
            CSV_IMPORT_ALERT_DIALOG_TAG -> delegateScope.launch {
                settingsOptionsUpdateInteractor.sendDismiss()
                settingsFileWorkDelegate.onCsvImportConfirmed()
            }
        }
    }

    override fun onDataExportSettingsSelected(data: DataExportSettingsResult) {
        when (data.tag) {
            CSV_EXPORT_DIALOG_TAG -> settingsFileWorkDelegate.onCsvExport(data)
            ICS_EXPORT_DIALOG_TAG -> settingsFileWorkDelegate.onIcsExport(data)
        }
    }

    override fun collapse() {
        isCollapsed = true
    }

    private fun onEditCategoriesClick() {
        router.navigate(CategoriesParams)
    }

    private fun onArchiveClick() {
        router.navigate(ArchiveParams)
    }

    private fun onCollapseClick() = delegateScope.launch {
        isCollapsed = isCollapsed.flip()
        parent?.updateContent()
    }

    private fun onBackupOptionsClick() {
        router.navigate(BackupOptionsParams)
    }

    private fun onExportCustomizedClick() {
        parent?.openOptions(OptionsContent.ExportAdvanced)
    }

    private fun onTriggerAutoExportClick() = delegateScope.launch {
        settingsOptionsUpdateInteractor.sendDismiss()
        settingsFileWorkDelegate.onTriggerAutoExportClick()
    }

    private fun onAutoBackupTriggerTimeClicked() {
        delegateScope.launch {
            settingsOpenDateTimeDialogRouter.openDateTimeDialog(
                tag = SettingsDialogTags.AUTO_BACKUP_TRIGGER_TIME_DIALOG_TAG,
                timestamp = prefsInteractor.getAutomaticBackupTriggerTime(),
                useMilitaryTime = prefsInteractor.getUseMilitaryTimeFormat(),
            )
        }
    }

    private fun onAutoExportTriggerTimeClicked() {
        delegateScope.launch {
            settingsOpenDateTimeDialogRouter.openDateTimeDialog(
                tag = SettingsDialogTags.AUTO_EXPORT_TRIGGER_TIME_DIALOG_TAG,
                timestamp = prefsInteractor.getAutomaticExportTriggerTime(),
                useMilitaryTime = prefsInteractor.getUseMilitaryTimeFormat(),
            )
        }
    }

    private fun onDateTimeSetDelegate(timestamp: Long, tag: String?) = delegateScope.launch {
        when (tag) {
            SettingsDialogTags.AUTO_BACKUP_TRIGGER_TIME_DIALOG_TAG -> {
                val newValue = settingsMapper.toStartOfDayShift(timestamp, wasPositive = true)
                prefsInteractor.setAutomaticBackupTriggerTime(newValue)
                automaticBackupInteractor.schedule()
                parent?.updateContent()
            }
            SettingsDialogTags.AUTO_EXPORT_TRIGGER_TIME_DIALOG_TAG -> {
                val newValue = settingsMapper.toStartOfDayShift(timestamp, wasPositive = true)
                prefsInteractor.setAutomaticExportTriggerTime(newValue)
                automaticExportInteractor.schedule()
                parent?.updateContent()
            }
        }
    }

    private fun onDateTimeFormatSelected(position: Int) {
        delegateScope.launch {
            val newData = settingsMapper.toCsvExportDateTimeFormat(position)
            prefsInteractor.setCsvExportDateTimeFormat(newData)
            parent?.updateContent()
        }
    }

    companion object : SettingsDelegate.Key {
        private const val CSV_EXPORT_DIALOG_TAG = "csv_export_dialog_tag"
        private const val ICS_EXPORT_DIALOG_TAG = "ics_export_dialog_tag"
        private const val BACKUP_RESTORE_DIALOG_TAG = "backup_restore_dialog_tag"
        private const val CSV_IMPORT_ALERT_DIALOG_TAG = "csv_import_alert_dialog_tag"
    }
}
