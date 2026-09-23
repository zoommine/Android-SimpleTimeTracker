package com.example.util.simpletimetracker.feature_settings.viewModel.delegate

import com.example.util.simpletimetracker.core.base.ViewModelDelegate
import com.example.util.simpletimetracker.core.provider.ApplicationDataProvider
import com.example.util.simpletimetracker.core.repo.ResourceRepo
import com.example.util.simpletimetracker.domain.extension.flip
import com.example.util.simpletimetracker.feature_settings.api.SettingsBlock
import com.example.util.simpletimetracker.feature_settings.R
import com.example.util.simpletimetracker.feature_settings.interactor.SettingsRatingViewDataInteractor
import com.example.util.simpletimetracker.navigation.Router
import com.example.util.simpletimetracker.navigation.params.action.OpenLinkParams
import com.example.util.simpletimetracker.navigation.params.action.OpenMarketParams
import com.example.util.simpletimetracker.navigation.params.action.SendEmailParams
import com.example.util.simpletimetracker.navigation.params.notification.SnackBarParams
import com.example.util.simpletimetracker.navigation.params.screen.DebugMenuDialogParams
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsRatingViewModelDelegate @Inject constructor(
    private val router: Router,
    private val resourceRepo: ResourceRepo,
    private val applicationDataProvider: ApplicationDataProvider,
    private val settingsRatingViewDataInteractor: SettingsRatingViewDataInteractor,
) : SettingsDelegate, ViewModelDelegate() {

    private var parent: SettingsParent? = null
    private var debugUnlocked = false
    private var debugClicksCount: Int = 0
    private var isCollapsed: Boolean = true

    override fun init(parent: SettingsParent) {
        this.parent = parent
    }

    override fun onHidden() {
        debugClicksCount = 0
    }

    override suspend fun getViewData(): SettingsDelegate.ViewData {
        return SettingsDelegate.ViewData(
            key = Companion,
            data = settingsRatingViewDataInteractor.execute(
                debugUnlocked = debugUnlocked,
                isCollapsed = isCollapsed,
            ),
        )
    }

    override fun onBlockClicked(block: SettingsBlock) {
        when (block) {
            SettingsBlock.RateUs -> onRateClick()
            SettingsBlock.SupportDevelopment -> onSupportDevelopmentClick()
            SettingsBlock.Feedback -> onFeedbackClick()
            // Version header: each click counts toward debug unlock;
            // once unlocked it also toggles the collapse to reveal Debug menu.
            SettingsBlock.RatingCollapse -> onVersionClick()
            SettingsBlock.DebugMenu -> onDebugMenuClick()
            else -> {
                // Do nothing
            }
        }
    }

    override fun collapse() {
        isCollapsed = true
    }

    private fun onRateClick() {
        router.execute(
            OpenMarketParams(packageName = applicationDataProvider.getPackageName()),
        )
    }

    private fun onSupportDevelopmentClick() {
        router.execute(
            OpenLinkParams(link = resourceRepo.getString(R.string.support_development_link)),
        )
    }

    private fun onFeedbackClick() {
        router.execute(
            data = SendEmailParams(
                email = resourceRepo.getString(R.string.support_email),
                subject = resourceRepo.getString(R.string.support_email_subject),
                chooserTitle = resourceRepo.getString(R.string.settings_email_chooser_title),
                notHandledCallback = { R.string.message_app_not_found.let(::showMessage) },
            ),
        )
    }

    private fun onVersionClick() {
        debugClicksCount += 1
        if (debugClicksCount >= DEBUG_CLICKS_TO_UNLOCK) {
            debugUnlocked = true
        }
        if (debugUnlocked) {
            // Toggle expansion to show/hide Debug menu.
            isCollapsed = isCollapsed.flip()
        }
        delegateScope.launch { parent?.updateContent() }
    }

    private fun onDebugMenuClick() {
        router.navigate(DebugMenuDialogParams)
    }

    private fun showMessage(stringResId: Int) {
        val params = SnackBarParams(message = resourceRepo.getString(stringResId))
        router.show(params)
    }

    companion object : SettingsDelegate.Key {
        private const val DEBUG_CLICKS_TO_UNLOCK = 5
    }
}