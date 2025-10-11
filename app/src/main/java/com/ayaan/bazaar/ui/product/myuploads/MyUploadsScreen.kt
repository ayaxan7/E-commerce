package com.ayaan.bazaar.ui.product.myuploads

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayaan.bazaar.ui.common.ErrorMessage
import com.ayaan.bazaar.ui.common.LoadingIndicator
import com.ayaan.bazaar.ui.product.myuploads.components.EmptyMyUploads
import com.ayaan.bazaar.ui.product.myuploads.components.MyUploadsList
import com.ayaan.bazaar.util.UiState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyUploadsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    viewModel: MyUploadsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deleteState by viewModel.deleteState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMyProducts()
    }

    // Handle delete state
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is UiState.Success -> {
                // Product deleted successfully, clear state
                viewModel.clearDeleteState()
            }

            is UiState.Error -> {
                // You can show a snackbar here if needed
                // For now, just clear the state after a delay
                delay(3000)
                viewModel.clearDeleteState()
            }

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Text(
                    text = "My Uploads",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
            )
        }) { paddingValues ->
        when (val state = uiState) {
            is UiState.Loading -> {
                LoadingIndicator()
            }

            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyMyUploads()
                } else {
                    MyUploadsList(
                        products = state.data,
                        onProductClick = onNavigateToProductDetail,
                        onDeleteProduct = { productId -> viewModel.deleteProduct(productId) },
                        isDeleting = deleteState is UiState.Loading,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }

            is UiState.Error -> {
                ErrorMessage(
                    message = state.message, onRetry = { viewModel.loadMyProducts() })
            }

            else -> Unit
        }

        // Show delete error if any
        if (deleteState is UiState.Error) {
            ErrorMessage(
                message = (deleteState as UiState.Error).message,
                onRetry = { viewModel.clearDeleteState() })
        }
    }
}





