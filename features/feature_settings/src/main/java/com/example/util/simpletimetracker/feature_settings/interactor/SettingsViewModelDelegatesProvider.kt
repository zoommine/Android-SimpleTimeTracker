package com.example.util.simpletimetracker.feature_settings.interactor

import com.example.util.simpletimetracker.core.base.ViewModelDelegate
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_base_adapter.dayOfWeek.DayOfWeekViewData
import com.example.util.simpletimetracker.feature_settings.api.SettingsBlock
import com.example.util.simpletimetracker.feature_settings.model.OptionsContent
import com.example.util.simpletimetracker.feature_settings.viewModel.delegate.SettingsUiDelegated
import com.example.util.simpletimetracker.feature_settings.viewModel.delegate.SettingsAdditionalViewModelDelegate
import com.example.util.simpletimetracker.feature_settings.viewModel.delegate.SettingsContributorsViewModelDelegate
import com.example.util.simpletimetracker.feature_settings.viewModel.delegate.SettingsDataManagementViewModelDelegate
import com.example.util.simpletimetracker.feature_settings.viewModel.delegate.SettingsDelegate
import com.example.util.simpletimetracker.feature_settings.viewModel.delegate.SettingsDisplayViewModelDelegate
import com.example.util.simpletimetracker.feature_settings.viewModel.delegate.SettingsMainViewModelDelegate
import com.example.util.simpletimetracker.feature_settings.viewModel.delegate.SettingsNotificationsViewModelDelegate
import com.example.util.simpletimetracker.feature_settings.viewModel.delegate.SettingsRatingViewModelDelegate
import com.example.util.simpletimetracker.feature_settings.viewModel.delegate.SettingsTranslatorsViewModelDelegate
import com.example.util.simpletimetracker.navigation.params.screen.DataExportSettingsResult
import com.example.util.simpletimetracker.navigation.params.screen.OptionsListParams
import javax.inject.Inject

class SettingsViewModelDelegatesProvider @Inject constructor(
    val mainDelegate: SettingsMainViewModelDelegate,
    val additionalDelegate: SettingsAdditionalViewModelDelegate,
    ratingDelegate: SettingsRatingViewModelDelegate,
    notificationsDelegate: SettingsNotificationsViewModelDelegate,
    displayDelegate: SettingsDisplayViewModelDelegate,
    dataManagementDelegate: SettingsDataManagementViewModelDelegate,
    translatorsDelegate: SettingsTranslatorsViewModelDelegate,
    contributorsDelegate: SettingsContributorsViewModelDelegate,
    // Kept for reference; content moved to SettingsDataManagementViewModelDelegate.
    // backupDelegate: SettingsBackupViewModelDelegate,
    // exportDelegate: SettingsExportViewModelDelegate,
) : SettingsUiDelegated {

    val delegates: List<SettingsDelegate> = listOf(
        mainDelegate,           // Handles: AllowMultitasking, DarkMode, Language (UI now in Display)
        notificationsDelegate,
        displayDelegate,
        additionalDelegate,
        dataManagementDelegate, // Categories, Archive, Backup, Export
        ratingDelegate,         // Version number (at bottom)
        translatorsDelegate,
        contributorsDelegate,
    )

    override fun onHidden() =
        delegates.forEach { it.onHidden() }

    override fun onBlockClicked(block: SettingsBlock) =
        delegates.forEach { it.onBlockClicked(block) }

    override fun onSpinnerPositionSelected(block: SettingsBlock, position: Int) =
        delegates.forEach { it.onSpinnerPositionSelected(block, position) }

    override fun onPositiveClick(tag: String?) =
        delegates.forEach { it.onPositiveClick(tag) }

    override fun onDurationSet(tag: String?, duration: Long) =
        delegates.forEach { it.onDurationSet(tag, duration) }

    override fun onDurationDisabled(tag: String?) =
        delegates.forEach { it.onDurationDisabled(tag) }

    override fun onDateTimeSet(timestamp: Long, tag: String?) =
        delegates.forEach { it.onDateTimeSet(timestamp, tag) }

    override fun onDataExportSettingsSelected(data: DataExportSettingsResult) =
        delegates.forEach { it.onDataExportSettingsSelected(data) }

    override fun onTypesSelected(typeIds: List<Long>, tag: String) =
        delegates.forEach { it.onTypesSelected(typeIds, tag) }

    override fun onOptionsItemClick(id: OptionsListParams.Item.Id) =
        delegates.forEach { it.onOptionsItemClick(id) }

    override fun onDayOfWeekClicked(block: SettingsBlock, data: DayOfWeekViewData) =
        delegates.forEach { it.onDayOfWeekClicked(block, data) }

    fun clear() =
        delegates.forEach { (it as? ViewModelDelegate)?.clear() }

    fun collapse() =
        delegates.forEach { it.collapse() }

    suspend fun loadContent(): List<ViewHolderType> {
        val order: List<SettingsDelegate.Key> = listOf(
            SettingsMainViewModelDelegate,           // produces emptyList; still handles DarkMode/Language/AllowMultitasking clicks
            SettingsNotificationsViewModelDelegate,  // 🔔 通知
            SettingsDisplayViewModelDelegate,        // 🖥️ 显示（含外观/语言/多任务）
            SettingsAdditionalViewModelDelegate,     // ⚙️ 其他设置
            SettingsDataManagementViewModelDelegate, // 🗄️ 备份与数据
            SettingsRatingViewModelDelegate,         // ℹ️ 版本号（最底部）
            SettingsTranslatorsViewModelDelegate,
            SettingsContributorsViewModelDelegate,
        )
        val viewData = delegates.map { it.getViewData() }.associateBy { it.key }
        return order.mapNotNull { key -> viewData[key]?.data }.flatten()
    }

    suspend fun loadOptionsContent(content: OptionsContent): List<ViewHolderType> {
        return delegates.map { it.getSheetViewData(content) }.firstOrNull { !it.isNullOrEmpty() }.orEmpty()
    }
}