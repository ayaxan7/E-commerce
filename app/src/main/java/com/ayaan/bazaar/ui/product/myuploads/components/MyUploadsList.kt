package com.ayaan.bazaar.ui.product.myuploads.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ayaan.bazaar.domain.model.Product

@Composable
fun MyUploadsList(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    onDeleteProduct: (String) -> Unit,
    isDeleting: Boolean,
    modifier: Modifier = Modifier
) {
    Log.d("MyUploadsScreen", "MyUploadsList: $products")
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) },
                onDelete = { onDeleteProduct(product.id) },
                isDeleting = isDeleting,
            )
        }
    }
}