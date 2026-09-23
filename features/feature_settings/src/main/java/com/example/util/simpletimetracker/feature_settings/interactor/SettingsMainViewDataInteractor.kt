package com.example.util.simpletimetracker.feature_settings.interactor

import com.example.util.simpletimetracker.core.interactor.LanguageInteractor
import com.example.util.simpletimetracker.core.repo.ResourceRepo
import com.example.util.simpletimetracker.domain.prefs.interactor.PrefsInteractor
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_settings.R
import com.example.util.simpletimetracker.feature_settings.api.SettingsBlock
import com.example.util.simpletimetracker.feature_settings.views.SettingsSpinnerNotCheckableViewData
import com.example.util.simpletimetracker.feature_settings.mapper.SettingsMapper
import com.example.util.simpletimetracker.feature_settings.viewData.DarkModeViewData
import com.example.util.simpletimetracker.feature_settings.viewData.LanguageViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsBottomViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsCheckboxViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsSpinnerViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsTextViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsTopViewData
import javax.inject.Inject

class SettingsMainViewDataInteractor @Inject constructor(
    private val resourceRepo: ResourceRepo,
    private val settingsMapper: SettingsMapper,
    private val prefsInteractor: PrefsInteractor,
    private val languageInteractor: LanguageInteractor,
) {

    suspend fun execute(): List<ViewHolderType> {
        // AllowMultitasking, DarkMode, Language → moved to Display settings section
        // Categories, Archive → moved to DataManagement section
        return emptyList()
    }

    private suspend fun loadDarkModeViewData(): DarkModeViewData {
        return prefsInteractor.getSelectedDarkMode()
            .let(settingsMapper::toDarkModeViewData)
    }

    private fun loadLanguageViewData(): LanguageViewData {
        return languageInteractor.getCurrentLanguage()
            .let(settingsMapper::toLanguageViewData)
    }
}