package com.example.util.simpletimetracker.feature_settings.interactor

import com.example.util.simpletimetracker.core.repo.ResourceRepo
import com.example.util.simpletimetracker.domain.prefs.interactor.PrefsInteractor
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_settings.R
import com.example.util.simpletimetracker.feature_settings.api.SettingsBlock
import com.example.util.simpletimetracker.feature_settings.mapper.SettingsMapper
import com.example.util.simpletimetracker.feature_settings.viewData.ExportDateTimeFormatViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsBottomViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsCheckboxViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsCollapseViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsHintViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsSelectorViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsSpinnerViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsTextColor
import com.example.util.simpletimetracker.feature_settings.views.SettingsTextViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsTextWithButtonViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsTopViewData
import javax.inject.Inject

class SettingsDataManagementViewDataInteractor @Inject constructor(
    private val resourceRepo: ResourceRepo,
    private val prefsInteractor: PrefsInteractor,
    private val settingsMapper: SettingsMapper,
    private val settingsCommonInteractor: SettingsCommonInteractor,
) {

    suspend fun execute(isCollapsed: Boolean): List<ViewHolderType> {
        val isDarkTheme = prefsInteractor.getDarkMode()
        val result = mutableListOf<ViewHolderType>()

        result += SettingsTopViewData(
            block = SettingsBlock.DataManagementTop,
        )

        result += SettingsCollapseViewData(
            block = SettingsBlock.DataManagementCollapse,
            title = resourceRepo.getString(R.string.settings_data_management_title),
            opened = !isCollapsed,
            iconResId = R.drawable.save,
            iconColor = (if (isDarkTheme) R.color.green_300 else R.color.green_200)
                .let(resourceRepo::getColor),
            dividerIsVisible = !isCollapsed,
        )

        if (!isCollapsed) {
            // --- Data: Categories & Archive ---
            result += SettingsTextViewData(
                block = SettingsBlock.Categories,
                title = resourceRepo.getString(R.string.settings_edit_categories),
                subtitle = resourceRepo.getString(R.string.settings_edit_categories_hint),
            )
            result += SettingsTextViewData(
                block = SettingsBlock.Archive,
                title = resourceRepo.getString(R.string.settings_archive),
                subtitle = "",
                dividerIsVisible = true,
            )

            // --- Backup ---
            result += SettingsTextViewData(
                block = SettingsBlock.BackupSave,
                title = resourceRepo.getString(R.string.settings_save_backup),
                subtitle = resourceRepo.getString(R.string.settings_save_description),
            )
            result += SettingsTextViewData(
                block = SettingsBlock.BackupRestore,
                title = resourceRepo.getString(R.string.settings_restore_backup),
                subtitle = resourceRepo.getString(R.string.settings_restore_description),
                subtitleColor = SettingsTextColor.Attention,
            )
            val automaticBackupEnabled = loadAutomaticBackupEnabled()
            val automaticBackupLastSaveTime = loadAutomaticBackupLastSaveTime()
            result += SettingsCheckboxViewData(
                block = SettingsBlock.BackupAutomatic,
                title = resourceRepo.getString(R.string.settings_automatic_backup),
                subtitle = resourceRepo.getString(R.string.settings_automatic_description),
                isChecked = automaticBackupEnabled,
                bottomSpaceIsVisible = !automaticBackupEnabled,
                dividerIsVisible = !automaticBackupEnabled,
                forceBind = true,
            )
            if (automaticBackupLastSaveTime.isNotEmpty()) {
                result += SettingsHintViewData(
                    block = SettingsBlock.BackupAutomaticHint,
                    text = automaticBackupLastSaveTime,
                    textColor = SettingsTextColor.Success,
                    topSpaceIsVisible = false,
                    dividerIsVisible = false,
                    bottomSpaceIsVisible = false,
                )
            }
            if (automaticBackupEnabled) {
                result += SettingsSelectorViewData(
                    block = SettingsBlock.BackupAutomaticTime,
                    title = resourceRepo.getString(R.string.settings_automatic_save_time),
                    subtitle = "",
                    selectedValue = loadAutomaticBackupTriggerTime(),
                    bottomSpaceIsVisible = true,
                    dividerIsVisible = true,
                )
            }
            result += SettingsTextViewData(
                block = SettingsBlock.BackupCustomized,
                title = resourceRepo.getString(R.string.settings_backup_options),
                subtitle = "",
                dividerIsVisible = true,
            )

            // --- Export & Import ---
            result += SettingsTextViewData(
                block = SettingsBlock.ExportSpreadsheet,
                title = resourceRepo.getString(R.string.settings_export_csv),
                subtitle = resourceRepo.getString(R.string.settings_export_csv_description),
                hint = resourceRepo.getString(R.string.settings_export_warning),
                hintColor = SettingsTextColor.Attention,
            )
            val automaticExportEnabled = loadAutomaticExportEnabled()
            val automaticExportLastSaveTime = loadAutomaticExportLastSaveTime()
            result += SettingsCheckboxViewData(
                block = SettingsBlock.ExportSpreadsheetAutomatic,
                title = resourceRepo.getString(R.string.settings_automatic_export),
                subtitle = resourceRepo.getString(R.string.settings_automatic_description),
                isChecked = automaticExportEnabled,
                bottomSpaceIsVisible = !automaticExportEnabled,
                dividerIsVisible = !automaticExportEnabled,
                forceBind = true,
            )
            if (automaticExportLastSaveTime.isNotEmpty()) {
                result += SettingsHintViewData(
                    block = SettingsBlock.ExportSpreadsheetAutomaticHint,
                    text = automaticExportLastSaveTime,
                    textColor = SettingsTextColor.Success,
                    topSpaceIsVisible = false,
                    dividerIsVisible = false,
                    bottomSpaceIsVisible = false,
                )
            }
            if (automaticExportEnabled) {
                result += SettingsSelectorViewData(
                    block = SettingsBlock.ExportSpreadsheetAutomaticTime,
                    title = resourceRepo.getString(R.string.settings_automatic_save_time),
                    subtitle = "",
                    selectedValue = loadAutomaticExportTriggerTime(),
                    bottomSpaceIsVisible = true,
                    dividerIsVisible = true,
                )
            }
            result += SettingsTextViewData(
                block = SettingsBlock.ExportCustomized,
                title = resourceRepo.getString(R.string.settings_backup_options),
                subtitle = "",
                dividerIsVisible = false,
            )
        }

        result += SettingsBottomViewData(
            block = SettingsBlock.DataManagementBottom,
        )

        return result
    }

    /** Bottom-sheet content for the advanced export options panel. */
    suspend fun executeAdvanced(): List<ViewHolderType> {
        val result = mutableListOf<ViewHolderType>()

        result += SettingsTextWithButtonViewData(
            buttonBlock = SettingsBlock.ExportSpreadsheetImportHint,
            data = SettingsTextViewData(
                block = SettingsBlock.ExportSpreadsheetImport,
                title = resourceRepo.getString(R.string.settings_import_csv),
                subtitle = resourceRepo.getString(R.string.settings_import_csv_description),
                hint = resourceRepo.getString(R.string.data_edit_hint),
                hintColor = SettingsTextColor.Attention,
            ),
        )

        val dateTimeFormatViewData = loadDateTimeFormatViewData()
        result += SettingsSpinnerViewData(
            block = SettingsBlock.ExportSpreadsheetDateTimeFormat,
            title = resourceRepo.getString(R.string.settings_export_csv_format),
            value = dateTimeFormatViewData.items
                .getOrNull(dateTimeFormatViewData.selectedPosition)?.text.orEmpty(),
            items = dateTimeFormatViewData.items,
            selectedPosition = dateTimeFormatViewData.selectedPosition,
            processSameItemSelected = false,
            dividerIsVisible = false,
            bottomSpaceIsVisible = false,
        )
        result += SettingsHintViewData(
            block = SettingsBlock.ExportSpreadsheetDateTimeFormatHint,
            text = loadDateTimeFormatHintViewData(),
            topSpaceIsVisible = false,
        )

        result += SettingsTextViewData(
            block = SettingsBlock.ExportIcs,
            title = resourceRepo.getString(R.string.settings_export_ics),
            subtitle = resourceRepo.getString(R.string.settings_export_warning),
            subtitleColor = SettingsTextColor.Attention,
        )

        if (loadAutomaticExportEnabled()) {
            result += SettingsTextViewData(
                block = SettingsBlock.ExportTriggerAutoBackup,
                title = resourceRepo.getString(R.string.backup_options_trigger_auto_export),
                subtitle = "",
            )
        }

        return result
    }

    // region Backup helpers

    private suspend fun loadAutomaticBackupEnabled(): Boolean =
        prefsInteractor.getAutomaticBackupUri().isNotEmpty()

    private suspend fun loadAutomaticBackupLastSaveTime(): String =
        if (loadAutomaticBackupEnabled()) {
            settingsCommonInteractor.getLastSaveString(
                prefsInteractor.getAutomaticBackupLastSaveTime(),
            )
        } else {
            ""
        }

    private suspend fun loadAutomaticBackupTriggerTime(): String =
        settingsMapper.toStartOfDayText(
            startOfDayShift = prefsInteractor.getAutomaticBackupTriggerTime(),
            useMilitaryTime = prefsInteractor.getUseMilitaryTimeFormat(),
        )

    // endregion

    // region Export helpers

    private suspend fun loadAutomaticExportEnabled(): Boolean =
        prefsInteractor.getAutomaticExportUri().isNotEmpty()

    private suspend fun loadAutomaticExportLastSaveTime(): String =
        if (loadAutomaticExportEnabled()) {
            settingsCommonInteractor.getLastSaveString(
                prefsInteractor.getAutomaticExportLastSaveTime(),
            )
        } else {
            ""
        }

    private suspend fun loadAutomaticExportTriggerTime(): String =
        settingsMapper.toStartOfDayText(
            startOfDayShift = prefsInteractor.getAutomaticExportTriggerTime(),
            useMilitaryTime = prefsInteractor.getUseMilitaryTimeFormat(),
        )

    private suspend fun loadDateTimeFormatViewData(): ExportDateTimeFormatViewData =
        prefsInteractor.getCsvExportDateTimeFormat()
            .let(settingsMapper::toCsvExportDateTimeFormat)

    private suspend fun loadDateTimeFormatHintViewData(): String =
        prefsInteractor.getCsvExportDateTimeFormat()
            .let(settingsMapper::toCsvExportDateTimeFormatHint)

    // endregion
}
