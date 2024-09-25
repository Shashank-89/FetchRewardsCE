package com.example.fetchrewardsce.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.fetchrewardsce.ui.model.Group
import kotlin.collections.forEach

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GroupedLazyColumn(
    groups: List<Group>,
    modifier: Modifier = Modifier,
){
    LazyColumn(modifier = modifier) {
        groups.forEach { group ->
            stickyHeader{
                GroupHeader(text = group.listInfo)
            }
            items( group.list ) { item ->
                GroupItem(text = "id: ${item.id}\nname: ${item.name}")
            }
        }
    }
}