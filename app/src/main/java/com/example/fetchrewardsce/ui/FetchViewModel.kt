package com.example.fetchrewardsce.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchrewardsce.data.repository.ItemRepository
import com.example.fetchrewardsce.data.Resource
import com.example.fetchrewardsce.ui.model.Group
import com.example.fetchrewardsce.util.ConnectivityObserver
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.text.get

class FetchViewModel(
    private val itemRepository: ItemRepository,
    connectivityObserver: ConnectivityObserver,
): ViewModel(){

    companion object{
        private const val LOG_TAG = "FetchViewModel"
    }

    private var _state = MutableStateFlow<Resource<List<Group>>>(Resource.Loading())

    val state: StateFlow<Resource<List<Group>>> = _state.onStart {
        refreshData()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    val networkStatus = connectivityObserver
        .observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectivityObserver.Status.Available)


    fun refreshData(forceRefresh: Boolean = false){
        viewModelScope.launch{
            onRefresh(forceRefresh).collect {
                Log.i(LOG_TAG, "onRefresh collecting: ${it::class.simpleName}")
                _state.value = it
            }
        }
    }

    private fun onRefresh(forceRefresh: Boolean = false): Flow<Resource<List<Group>>> =
        itemRepository.getDataWithFlow(forceRefresh, networkStatus).map { resource ->
            when(resource){
                is Resource.Error -> Resource.Error<List<Group>>(resource.message)
                is Resource.Loading -> Resource.Loading<List<Group>>()
                is Resource.Success -> Resource.Success<List<Group>>(
                    resource.data!!.map {
                        Group(
                            listInfo = "ListId: ${it.key}",
                            list = it.value
                        )
                    }
                )
            }
        }


//    private fun onRefresh(forceRefresh: Boolean = false): Flow<Resource<List<Group>>> {
//        Log.i(LOG_TAG, "onRefresh fr: $forceRefresh")
//        return itemRepository.getData(forceRefresh, networkStatus, viewModelScope).catch { e ->
//            Log.e(LOG_TAG, e.toString())
//        }.map { resource ->
//            when(resource){
//                is Resource.Error -> Resource.Error<List<Group>>(resource.message)
//                is Resource.Loading -> Resource.Loading<List<Group>>()
//                is Resource.Success -> Resource.Success<List<Group>>(
//                    resource.data!!.map {
//                        Group(
//                            listInfo = "ListId: ${it.key}",
//                            list = it.value
//                        )
//                    }
//                )
//            }
//        }
//    }

}