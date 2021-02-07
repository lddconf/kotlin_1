package com.example.notes.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.example.notes.R
import com.example.notes.databinding.ActivitySplashBinding
import com.example.notes.model.auth.NoAuthException
import com.example.notes.ui.viewmodel.SplashViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar

private const val LOGIN_RC = 782
private const val REQUEST_DELAY_MS = 1000L

class SplashActivity : BaseActivity<Boolean?, SplashViewState>() {

    override val viewModel: SplashViewModel
            by lazy {
                ViewModelProvider(this).get(SplashViewModel::class.java)
            }

    override val layoutResourceId: Int = R.layout.activity_splash

    override val ui : ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper())
            .postDelayed({
                viewModel.requestUser()
            }, REQUEST_DELAY_MS
        )
    }

    override fun renderData(data: Boolean?) {
        data?.takeIf { it }?.let {
            startMainActivity()
        }
    }

    override fun renderError(error: Throwable) {
        when (error) {
            is NoAuthException -> startLoginActivity()
            else ->
                Snackbar.make(
                    ui.splashLayout,
                    error.message.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
        }
    }

    private fun startMainActivity() {
        startActivity(MainActivity.getStartIntent(this ))
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
            LOGIN_RC )
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