package com.example.fetchrewardsce.data.repository

import android.util.Log
import com.example.fetchrewardsce.data.Resource
import com.example.fetchrewardsce.data.local.ItemDatabase
import com.example.fetchrewardsce.data.local.ItemEntity
import com.example.fetchrewardsce.data.remote.RemoteAPIs
import com.example.fetchrewardsce.data.remote.dto.FetchResponse
import com.example.fetchrewardsce.data.remote.dto.ItemDTO
import com.example.fetchrewardsce.data.toItemEntity
import com.example.fetchrewardsce.ui.FetchViewModel
import com.example.fetchrewardsce.util.ConnectivityObserver
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import retrofit2.Response
import kotlin.coroutines.coroutineContext

class ItemRepository(
    val db: ItemDatabase
) {

    private fun getCachedData(): Flow<Map<Int, List<ItemEntity>>> = db.itemDao.getAllItems().distinctUntilChanged()
        .map { items ->
            items.groupBy { it.listId }.toSortedMap()
        }

    private suspend fun refreshCache(): Response<List<ItemDTO>> {
        val response = RemoteAPIs.fetchAPI.getData()
        if (response.isSuccessful) {
            response.body()?.let { list ->
                list.filter { !it.name.isNullOrBlank() }
                    .map { it.toItemEntity() }
                    .also { itemEntityList ->
                        db.itemDao.addAllItems(itemEntityList)
                    }
            }
        }
        return response
    }


    fun getDataWithFlow(
        refresh: Boolean = false,
        networkStatus: StateFlow<ConnectivityObserver.Status>
    ): Flow<Resource<Map<Int, List<ItemEntity>>>> = flow {
        emit(Resource.Loading())

        // Refresh cache if needed
        if (refresh || getCachedData().first().isEmpty()) {

            if(networkStatus.value != ConnectivityObserver.Status.Available){
                emit(Resource.Error("Failed to refresh data: no internet connection!!"))
                return@flow
            }

            val response = runCatching { refreshCache() }.getOrElse {
                emit(Resource.Error("Failed to refresh data: ${it.localizedMessage}"))
                return@flow
            }
            if (!response.isSuccessful) {
                emit(Resource.Error(response.message() ?: "Unknown error occurred"))
                return@flow
            }
        }

        // Collect and emit cached data
        getCachedData()
            .map { map ->
                if (map.isNotEmpty()) Resource.Success(map) else Resource.Error("No data available")
            }
            .catch { emit(Resource.Error("Failed to get cached data: ${it.localizedMessage}")) }
            .collect { emit(it) }
    }
}