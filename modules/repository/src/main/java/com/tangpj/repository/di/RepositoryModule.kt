package com.tangpj.repository.di

import androidx.room.Room
import com.apollographql.apollo.ApolloClient
import com.tangpj.github.BuildConfig
import com.tangpj.github.GithubApp
import com.tangpj.github.core.apollo.DateCustomerAdapter
import com.tangpj.repository.db.RepoDao
import com.tangpj.repository.db.RepositoryDb
import com.tangpj.repository.type.CustomType
import com.tangpj.repository.ui.repositories.RepoFragment
import dagger.Module
import dagger.Provides

@Module(includes = [
    ViewModelModule::class])
class RepositoryModule{

    @RepositoryScope
    @Provides
    fun providerRepositoryDb(app: GithubApp) =
            Room.databaseBuilder(app, RepositoryDb::class.java, "repository.db")
                    .fallbackToDestructiveMigration()
                    .build()

    @RepositoryScope
    @Provides
    fun providerRepoDao(repositoryDb: RepositoryDb): RepoDao =
            repositoryDb.repoDao()

    @RepositoryScope
    @Suppress("HasPlatformType")
    @Provides
    fun providerRepositoryApollo(
            apolloClientBuilder: ApolloClient.Builder,
            datetimeAdapter: DateCustomerAdapter) =
        apolloClientBuilder
                .serverUrl(BuildConfig.BASE_URL)
                .addCustomTypeAdapter(CustomType.DATETIME, datetimeAdapter)
                .build()

}