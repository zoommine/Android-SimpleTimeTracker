package com.example.util.simpletimetracker.feature_settings.interactor

import com.example.util.simpletimetracker.core.interactor.LanguageInteractor
import com.example.util.simpletimetracker.core.repo.ResourceRepo
import com.example.util.simpletimetracker.feature_settings.api.SettingsBlock
import com.example.util.simpletimetracker.domain.language.AppLanguage
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_settings.R
import com.example.util.simpletimetracker.feature_settings.views.SettingsBottomViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsTextViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsTopViewData
import com.example.util.simpletimetracker.feature_settings.views.SettingsTranslatorViewData
import javax.inject.Inject

class SettingsTranslatorsViewDataInteractor @Inject constructor(
    private val resourceRepo: ResourceRepo,
    private val languageInteractor: LanguageInteractor,
) {

    fun execute(): List<ViewHolderType> {
        // Translators settings removed per user request
        return emptyList()
    }

    private fun loadTranslatorsViewData(): List<SettingsTranslatorViewData> {
        val nonTranslatable = listOf(AppLanguage.System, AppLanguage.English)

        return LanguageInteractor.languageList
            .filter { it !in nonTranslatable }
            .map {
                SettingsTranslatorViewData(
                    translator = languageInteractor.getTranslators(it),
                    language = languageInteractor.getDisplayName(it),
                )
            }
    }
}