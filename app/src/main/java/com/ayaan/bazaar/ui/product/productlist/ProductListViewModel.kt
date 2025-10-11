package com.ayaan.bazaar.ui.product.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.bazaar.domain.model.Product
import com.ayaan.bazaar.domain.repository.AuthRepository
import com.ayaan.bazaar.domain.repository.ProductRepository
import com.ayaan.bazaar.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> = _refreshing.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                productRepository.getProducts().collect { products ->
                    _uiState.value = UiState.Success(products)
                    _refreshing.value = false
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load products")
                _refreshing.value = false
            }
        }
    }

    fun refresh() {
        _refreshing.value = true
        loadProducts()
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
