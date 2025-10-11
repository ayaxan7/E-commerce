package com.ayaan.bazaar.ui.product.productdetails

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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayaan.bazaar.ui.common.ErrorMessage
import com.ayaan.bazaar.ui.common.LoadingIndicator
import com.ayaan.bazaar.ui.product.productdetails.components.ProductDetailContent
import com.ayaan.bazaar.util.UiState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: ProductDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                ProductDetailContent(
                    product = state.data, onContactSeller = { email ->
                        clipboardManager.setText(AnnotatedString(email))
                    }, modifier = Modifier.padding(paddingValues)
                )
            }

            is UiState.Error -> {
                ErrorMessage(
                    message = state.message, onRetry = { viewModel.loadProduct(productId) })
            }

            else -> Unit
        }
    }
}