package com.tangpj.oauth2.ui

import androidx.lifecycle.ViewModel
import com.tangpj.oauth2.di.Oauth2Scope
import com.tangpj.recurve.dagger2.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class AuthorizationModule{

    @Oauth2Scope
    @ContributesAndroidInjector
    abstract fun contributeAuthorizationActivity(): AuthorizationActivity

    @Oauth2Scope
    @ContributesAndroidInjector
    abstract fun contributesAuthorizationFragment(): AuthorizationFragment

    @Binds
    @IntoMap
    @ViewModelKey(AuthorizationViewModel::class)
    abstract fun bindAtuhorizationViewModel(authorizationViewModel: AuthorizationViewModel): ViewModel
}