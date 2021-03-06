package com.fenchtose.movieratings.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.fenchtose.movieratings.BuildConfig
import com.fenchtose.movieratings.MainActivity
import com.fenchtose.movieratings.base.router.Router
import com.fenchtose.movieratings.features.moviepage.MoviePageFragment

class IntentUtils {

    companion object {

        val PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID

        fun launch3rdParty(context: Context, packageName: String) {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }

        fun openPlaystore(context: Context) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PLAYSTORE_URL))
            if (canStartIntent(context, intent)) {
                context.startActivity(intent)
            }
        }

        fun openShareIntent(context: Context, message: String) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "text/plain"
            context.startActivity(Intent.createChooser(intent, "Share via"))
        }

        fun openShareFileIntent(context: Context, uri: Uri) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.putExtra(Intent.EXTRA_TITLE, "flutter_data.txt")
            intent.type = "text/plain"
            context.startActivity(Intent.createChooser(intent, "Share via"))
        }

        fun getFileSelectionIntent(): Intent {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/plain"
            return intent
        }

        fun getFileCreationIntent(filename: String): Intent {
            return Intent(Intent.ACTION_CREATE_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_TITLE, filename)
        }

        fun canStartIntent(context: Context, intent: Intent): Boolean {
            return intent.resolveActivity(context.packageManager) != null
        }

        fun openImdb(context: Context, imdb: String?, newTask: Boolean = true): Boolean {
            imdb?.let {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/$imdb"))
                if (newTask) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(intent, "Open IMDb page with").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return true
            }

            return false
        }

        fun openMovie(context: Context, imdb: String?, inApp: Boolean = false, newTask: Boolean = true): Boolean {
            imdb?.let {
                if (!inApp) {
                    return openImdb(context, imdb, newTask)
                }

                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra(Router.HISTORY,
                        Router.History()
                                .addPath(MoviePageFragment.MoviePath.KEY,
                                        MoviePageFragment.MoviePath.createExtras(it))
                                .toBundle())
                if (newTask) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                context.startActivity(intent)
                return true
            }

            return false
        }
    }
}