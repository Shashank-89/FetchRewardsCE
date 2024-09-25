package com.example.fetchrewardsce

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fetchrewardsce.data.repository.ItemRepository
import com.example.fetchrewardsce.data.Resource
import com.example.fetchrewardsce.data.local.ItemDatabase
import com.example.fetchrewardsce.ui.FetchVMProviderFactory
import com.example.fetchrewardsce.ui.FetchViewModel
import com.example.fetchrewardsce.ui.components.GroupedLazyColumn
import com.example.fetchrewardsce.ui.theme.FetchRewardsCETheme
import com.example.fetchrewardsce.util.AndroidNetworkObserver

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FetchViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val itemRepository = ItemRepository(ItemDatabase(this))
        val vmProviderFactory = FetchVMProviderFactory(itemRepository, AndroidNetworkObserver(this))
        viewModel = ViewModelProvider(this, vmProviderFactory)[FetchViewModel::class.java]

        val title = resources.getString(R.string.app_name)

        setContent {
            FetchRewardsCETheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary),
                            title = { Text(title) },
                            // Provide an accessible alternative to trigger refresh.
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { viewModel.refreshData(true) },
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            shape = CircleShape,
                        ) {
                            Icon(Icons.Filled.Refresh, "Trigger Refresh")
                        }
                    }
                ){ innerPadding ->

                    val state = viewModel.state.collectAsStateWithLifecycle()
                    viewModel.networkStatus.collectAsStateWithLifecycle()

                    when(state.value){
                        is Resource.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                            ){
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = state.value.message
                                )
                            }

                        }
                        is Resource.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                            ){
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .width(64.dp)
                                        .align(Alignment.Center),
                                    color = MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }

                        }
                        is Resource.Success -> {
                            GroupedLazyColumn(
                                modifier = Modifier.padding(vertical = innerPadding.calculateTopPadding()),
                                groups = state.value.data!!,
                            )
                        }
                    }

                }
            }
        }
    }
}
