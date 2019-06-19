package com.tangpj.repository.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.ItemKeyedDataSource
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.tangpj.paging.ItemKeyedBoundResource
import com.tangpj.repository.vo.Repo
import com.tangpj.recurve.apollo.LiveDataApollo

import com.tangpj.recurve.resource.ApiResponse
import com.tangpj.recurve.util.RateLimiter
import com.tangpj.repository.db.RepositoryDb
import com.tangpj.repository.valueObject.result.StarRepoResult
import com.tangpj.repository.mapper.*
import com.tangpj.repository.type.OrderDirection
import com.tangpj.repository.type.StarOrder
import com.tangpj.repository.type.StarOrderField
import com.tangpj.repository.StartRepositoriesQuery
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RepoRepository @Inject constructor(
         val apolloClient: ApolloClient,
         val repoDb: RepositoryDb){

    private val repoRateLimiter = RateLimiter<StartRepositoriesQuery>(1, TimeUnit.MILLISECONDS)

    fun loadStarRepos(login: String) =
            object : ItemKeyedBoundResource<String, Repo, StartRepositoriesQuery.Data>(){

                private var repoResult: StarRepoResult? = null

                val order = StarOrder
                        .builder()
                        .field(StarOrderField.STARRED_AT)
                        .direction(OrderDirection.DESC).build()

                var query: StartRepositoriesQuery? = null

                override fun saveCallResult(item: StartRepositoriesQuery.Data) {
                    repoResult = saveStarRepo(item, repoResult)
                    Timber.d("saveCallResult, pageInfo = ${repoResult?.pageInfo}")
                }

                override fun shouldFetch(data: List<Repo>?): Boolean =
                        (data == null || data.isEmpty() || repoRateLimiter.shouldFetch(query))

                override fun loadFromDb(): LiveData<List<Repo>>  {
                    val repoResultLive = repoDb.repoDao().loadStarRepoResult(login)
                    return Transformations.switchMap(repoResultLive){
                        repoDb.repoDao().loadRepoOrderById(it?.repoIds ?: emptyList())
                    }
                }

                override fun hasNextPage(): Boolean {
                    return repoResult?.pageInfo?.hasNextPage ?: false
                }

                override fun createInitialCall(params: ItemKeyedDataSource.LoadInitialParams<String>): LiveData<ApiResponse<StartRepositoriesQuery.Data>> {
                    val initialQuery = StartRepositoriesQuery.builder()
                            .login(login)
                            .startFirst(params.requestedLoadSize)
                            .order(order).build()
                    query = initialQuery
                    Timber.d("createInitialCall, start first = ${params.requestedLoadSize}, hasNextPage = ${repoResult?.pageInfo?.hasNextPage}")
                    val repoCall = apolloClient
                            .query(initialQuery)
                            .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                    return LiveDataApollo.from(repoCall)
                }

                @SuppressLint("BinaryOperationInTimber")
                override fun createAfterCall(params: ItemKeyedDataSource.LoadParams<String>): LiveData<ApiResponse<StartRepositoriesQuery.Data>> {
                    val afterQuery = StartRepositoriesQuery.builder()
                            .login(login)
                            .startFirst(params.requestedLoadSize)
                            .after(repoResult?.pageInfo?.endCursor)
                            .order(order).build()
                    query = afterQuery

                    val repoCall = apolloClient
                            .query(afterQuery)
                            .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
                    return LiveDataApollo.from(repoCall)
                }

                override fun getKey(item: Repo): String = item.id

            }.asListing( Config(
                    pageSize = 20,
                    enablePlaceholders = false,
                    initialLoadSizeHint = 20))


    private fun saveStarRepo(data: StartRepositoriesQuery.Data, starRepoResult: StarRepoResult?): StarRepoResult?{
        var result: StarRepoResult? = null
        Timber.d("saveStarRepo:  size: %d",data.mapperToRepoVoList().size)
        val repoList = data.mapperToRepoVoList()
        val repoIds = repoList.map { it.id }
        val starRepoResult = StarRepoResult(
                login = data.user?.login ?: "",
                repoIds = repoIds,
                pageInfo = data.getPageInfo())
        repoDb.runInTransaction {
        repoDb.repoDao().insertRepos(repoList)
        repoDb.repoDao().insertUserRepoResult(starRepoResult)
        }
        return result
    }

}