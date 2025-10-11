package com.ayaan.bazaar.ui.product.productdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.bazaar.domain.model.Product
import com.ayaan.bazaar.domain.repository.ProductRepository
import com.ayaan.bazaar.util.Result
import com.ayaan.bazaar.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val uiState: StateFlow<UiState<Product>> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = productRepository.getProduct(productId)) {
                is Result.Success -> {
                    _uiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(result.exception.message ?: "Failed to load product")
                }
            }
        }
    }
}
