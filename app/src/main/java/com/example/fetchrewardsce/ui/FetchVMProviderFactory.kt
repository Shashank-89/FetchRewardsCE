package com.example.fetchrewardsce.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fetchrewardsce.data.repository.ItemRepository
import com.example.fetchrewardsce.util.ConnectivityObserver

class FetchVMProviderFactory(
    val itemRepository: ItemRepository,
    val connectivityObserver: ConnectivityObserver,
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FetchViewModel(itemRepository, connectivityObserver) as T
    }
}