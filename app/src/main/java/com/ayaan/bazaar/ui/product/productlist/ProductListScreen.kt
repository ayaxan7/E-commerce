package com.ayaan.bazaar.ui.product.productlist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayaan.bazaar.domain.model.Product
import com.ayaan.bazaar.ui.common.ErrorMessage
import com.ayaan.bazaar.ui.common.LoadingIndicator
import com.ayaan.bazaar.ui.product.productlist.components.EmptyProductList
import com.ayaan.bazaar.ui.product.productlist.components.ProductGrid
import com.ayaan.bazaar.ui.theme.Blue500
import com.ayaan.bazaar.util.UiState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onNavigateToCreateProduct: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToMyUploads: () -> Unit,
    viewModel: ProductListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        TopAppBar(
            title = {
            Text(
                text = "🛒 Bazaar",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }, actions = {
            IconButton(onClick = onNavigateToMyUploads) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "My Uploads",
                    tint = Blue500
                )
            }
            IconButton(onClick = onNavigateToAuth) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sign Out",
                    tint = Blue500
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = onNavigateToCreateProduct,
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Product",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(refreshing),
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(paddingValues)
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    LoadingIndicator()
                }

                is UiState.Success -> {
                    if ((uiState as UiState.Success<List<Product>>).data.isEmpty()) {
                        EmptyProductList(onAddProduct = onNavigateToCreateProduct)
                    } else {
                        ProductGrid(
                            products = (uiState as UiState.Success<List<Product>>).data,
                            onProductClick = onNavigateToProductDetail
                        )
                    }
                }

                is UiState.Error -> {
                    ErrorMessage(
                        message = (uiState as UiState.Error).message,
                        onRetry = { viewModel.loadProducts() })
                }

                else -> Unit
            }
        }
    }
}