package com.example.notes.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.notes.R
import com.example.notes.databinding.ActivitySplashBinding
import com.example.notes.model.auth.NoAuthException
import com.example.notes.ui.viewmodel.SplashViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

private const val LOGIN_RC = 782
private const val REQUEST_DELAY_MS = 1000L

class SplashActivity : BaseActivity<Boolean>() {

    override val viewModel: SplashViewModel by viewModel()

    override val layoutResourceId: Int = R.layout.activity_splash

    override val ui: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        launch {
            delay(REQUEST_DELAY_MS)
            viewModel.requestUser()
        }
    }

    override fun renderData(data: Boolean) {
        if (data) {
            startMainActivity()
        }
    }


    override fun renderError(error: Throwable) {
        when (error) {
            is NoAuthException -> startLoginActivity()
            else ->
                Snackbar.make(
                    ui.root,
                    error.message.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
        }
    }

    private fun startMainActivity() {
        startActivity(MainActivity.getStartIntent(this))
    }

    private fun startLoginActivity() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.common_google_signin_btn_icon_dark)
                .setTheme(R.style.SplashTheme)
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build()
                    )
                )
                .build(),
            LOGIN_RC
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_RC && resultCode != Activity.RESULT_OK) {
            finish()
        }
    }


    companion object {
        fun getStartIntent(context: Context) = Intent(context, SplashActivity::class.java)
    }
}