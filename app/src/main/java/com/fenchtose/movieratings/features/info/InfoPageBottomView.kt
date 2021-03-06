package com.fenchtose.movieratings.features.info

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.fenchtose.movieratings.BuildConfig
import com.fenchtose.movieratings.MovieRatingsApplication
import com.fenchtose.movieratings.R
import com.fenchtose.movieratings.analytics.ga.GaEvents
import com.fenchtose.movieratings.base.router.Router
import com.fenchtose.movieratings.features.premium.DonatePageFragment
import com.fenchtose.movieratings.features.settings.SettingsFragment
import com.fenchtose.movieratings.model.inAppAnalytics.DbHistoryKeeper
import com.fenchtose.movieratings.util.Constants
import com.fenchtose.movieratings.util.IntentUtils

class InfoPageBottomView: LinearLayout {

    private var router: Router? = null
    private var category: String? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.info_page_bottom_container_layout, this, true)
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))

        val historyKeeper = DbHistoryKeeper.newInstance(MovieRatingsApplication.instance!!)

        val premiumView: View = findViewById(R.id.premium_view)
        if (BuildConfig.FLAVOR == "playstore") {
            premiumView.visibility = View.VISIBLE
            premiumView.setOnClickListener {
                GaEvents.OPEN_SUPPORT_APP.withCategory(category).track()
                router?.go(DonatePageFragment.DonatePath())
            }
        }

        findViewById<View>(R.id.credit_view).setOnClickListener {
            showCreditsDialog()
        }

        findViewById<View>(R.id.rate_view).setOnClickListener {
            GaEvents.TAP_RATE_APP.withCategory(category).track()
            historyKeeper.ratedAppOnPlaystore()
            IntentUtils.openPlaystore(context)
        }

        val share = findViewById<View>(R.id.share_view)
        share?.let {
            share.setOnClickListener {
                GaEvents.TAP_SHARE_APP.withCategory(category).track()
                IntentUtils.openShareIntent(context, context.getString(R.string.info_page_share_content, Constants.APP_SHARE_URL))
            }
        }

        val settingsView = findViewById<View?>(R.id.settings_view)
        settingsView?.setOnClickListener {
            GaEvents.OPEN_SETTINGS.withCategory(category).track()
            router?.go(SettingsFragment.SettingsPath())
        }
    }

    fun setRouter(router: Router?, category: String?) {
        this.router = router
        this.category = category
    }

    private fun showCreditsDialog() {
        AlertDialog.Builder(context)
                .setTitle(R.string.credit_dialog_title)
                .setMessage(R.string.credit_dialog_content)
                .show()
    }
}