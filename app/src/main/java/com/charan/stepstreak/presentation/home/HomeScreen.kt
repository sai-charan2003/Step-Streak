package com.charan.stepstreak.presentation.home

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeScreenViewModel = hiltViewModel()
){
    val state = viewModel.state.collectAsState()
    val effects = viewModel.effects.collectAsState(initial = null)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text("Steps")
                },
                actions = {

                }


            )

        }
    ) { padding->
        LazyColumn (modifier = Modifier.padding(padding)){
            items(state.value.stepsData.size){ stepsData ->
                StepsRecordItem(stepsRecord = state.value.stepsData[stepsData])
            }
        }

    }

}
