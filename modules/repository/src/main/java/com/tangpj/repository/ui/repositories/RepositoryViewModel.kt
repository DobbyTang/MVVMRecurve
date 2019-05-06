package com.tangpj.repository.ui.repositories

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.tangpj.paging.Listing
import com.tangpj.paging.PageLoadState
import com.tangpj.recurve.resource.Resource
import com.tangpj.repository.repository.RepoRepository
import com.tangpj.repository.vo.RepoVo
import javax.inject.Inject

class RepositoryViewModel @Inject constructor(private val repoRepository: RepoRepository): ViewModel(){

    private val _login = MutableLiveData<String>()



    private val repoListing: LiveData<Listing<RepoVo>> = Transformations.map(_login){
        repoRepository.loadStarRepos(it)
    }

    var repoRetry: (() -> Unit)? = repoListing.value?.retry

    var refresh: (() -> Unit)? = repoListing.value?.refresh

    val repoResource: LiveData<Resource<PagedList<RepoVo>>> = Transformations.switchMap(repoListing){

        it.resource
    }

    val pageLoadState: LiveData<PageLoadState> = Transformations.switchMap(repoListing) {
        it?.pageLoadState
    }

    fun setRepoOwner(login: String){
        _login.value = login
    }


}