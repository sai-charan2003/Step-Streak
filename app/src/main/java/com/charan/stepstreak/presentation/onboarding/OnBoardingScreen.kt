package com.charan.stepstreak.presentation.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun OnBoardingScreen(
    navHostController: NavHostController,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val pageState = rememberPagerState(initialPage =  0 , pageCount = {3})

    Scaffold { padding->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            HorizontalPager(
                state = pageState
            ) { page->

                when(page){
                    0 -> IntoPage()
                    1 -> HealthConnectPermissionScreen()

                }
            }

        }
    }


}

@Preview(showBackground = true,uiMode = 1)
@Composable
fun OnBoardingScreenPreview(){
    OnBoardingScreen(navHostController = rememberNavController())

}

@Composable
fun IntoPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to StepStreak",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HealthConnectPermissionScreen(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Connect to health connect to get steps",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}
