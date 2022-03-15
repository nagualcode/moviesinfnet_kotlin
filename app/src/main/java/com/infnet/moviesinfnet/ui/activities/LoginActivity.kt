package com.infnet.moviesinfnet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.infnet.moviesinfnet.R
import com.infnet.moviesinfnet.databinding.ActivityLoginBinding
import com.infnet.moviesinfnet.utils.ext.exhaustive
import com.infnet.moviesinfnet.utils.ext.hideKeyboard
import com.infnet.moviesinfnet.utils.ext.tryShowSnackbarOK
import com.infnet.moviesinfnet.utils.helper.viewBinding
import com.infnet.moviesinfnet.utils.status.EventMessageStatus
import com.infnet.moviesinfnet.vm.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : AppCompatActivity()
{
    private val viewModel: LoginViewModel by viewModels()
    private val binding by viewBinding(ActivityLoginBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Infnet)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupCollecting()
    }

    private fun setupCollecting()
    {
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                /**
                 * check if user is logged.
                 * if so change activity
                 */
                it?.let {
                    signIn()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginState.collectLatest {
                Timber.d("LoginState collected $it")
                when (it)
                {
                    LoginViewModel.LoginState.LOGIN ->
                    {
                        binding.viewSwitcher.displayedChild = 0
                        setAnimation(true)
                    }
                    LoginViewModel.LoginState.REGISTRATION ->
                    {
                        binding.viewSwitcher.displayedChild = 1
                        setAnimation(false)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.state.collectLatest {
                applyState(it)
            }
        }
    }


    private fun setAnimation(animToLeft: Boolean)
    {
        with(binding.viewSwitcher)
        {
            inAnimation = AnimationUtils.loadAnimation(
                this@LoginActivity,
                if (animToLeft) R.anim.slide_in_right else R.anim.slide_in_left
            )
            outAnimation = AnimationUtils.loadAnimation(
                this@LoginActivity,
                if (animToLeft) R.anim.slide_out_left else R.anim.slide_out_right
            )
        }
    }

    private fun signIn()
    {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun applyState(event: EventMessageStatus)
    {
        when (event)
        {
            EventMessageStatus.Sleep ->
            {
                binding.proBarLoading.isVisible = false
            }
            EventMessageStatus.Loading ->
            {
                binding.proBarLoading.isVisible = true
                hideKeyboard()
            }
            is EventMessageStatus.Success ->
            {
                binding.proBarLoading.isVisible = false
                tryShowSnackbarOK(binding.cdRoot, event.successEvent)
            }
            is EventMessageStatus.Failed ->
            {
                binding.proBarLoading.isVisible = false
                tryShowSnackbarOK(binding.cdRoot, event.errorEvent)
            }
        }.exhaustive
    }


}