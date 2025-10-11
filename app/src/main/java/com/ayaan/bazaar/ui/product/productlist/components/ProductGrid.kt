package com.ayaan.bazaar.ui.product.productlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ayaan.bazaar.domain.model.Product

@Composable
fun ProductGrid(
    products: List<Product>, onProductClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product, onClick = { onProductClick(product.id) })
        }

        // Handle odd number of items - add empty spacer for last item to center it
        if (products.size % 2 != 0) {
            item {
                Spacer(modifier = Modifier)
            }
        }
    }
}