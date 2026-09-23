package com.example.util.simpletimetracker.feature_settings.interactor

import com.example.util.simpletimetracker.core.provider.ApplicationDataProvider
import com.example.util.simpletimetracker.core.repo.ResourceRepo
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_settings.R
import com.example.util.simpletimetracker.feature_settings.api.SettingsBlock
import com.example.util.simpletimetracker.feature_settings.views.SettingsBottomViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsCollapseViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsTextViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsTopViewData
import javax.inject.Inject

class SettingsRatingViewDataInteractor @Inject constructor(
    private val resourceRepo: ResourceRepo,
    private val applicationDataProvider: ApplicationDataProvider,
) {

    fun execute(
        debugUnlocked: Boolean,
        isCollapsed: Boolean,
    ): List<ViewHolderType> {
        val isDarkTheme = false // version card is neutral color
        val result = mutableListOf<ViewHolderType>()

        result += SettingsTopViewData(
            block = SettingsBlock.RatingTop,
        )

        // Version number styled the same as other section headers (collapse card style).
        // Clicking it 5 times unlocks the debug menu (handled in delegate).
        result += SettingsCollapseViewData(
            block = SettingsBlock.RatingCollapse,
            title = resourceRepo.getString(R.string.settings_version) +
                "  " + loadVersionName(),
            opened = !isCollapsed,
            iconResId = R.drawable.info,
            iconColor = resourceRepo.getColor(R.color.blue_300),
            dividerIsVisible = !isCollapsed && debugUnlocked,
            arrowIsVisible = debugUnlocked,
        )

        // Debug menu — only visible when unlocked AND section is expanded.
        if (debugUnlocked && !isCollapsed) {
            result += SettingsTextViewData(
                block = SettingsBlock.DebugMenu,
                title = resourceRepo.getString(R.string.debug_menu),
                subtitle = "",
                dividerIsVisible = false,
            )
        }

        result += SettingsBottomViewData(
            block = SettingsBlock.RatingBottom,
        )

        return result
    }

    private fun loadVersionName(): String {
        return applicationDataProvider.getAppVersion()
    }
}