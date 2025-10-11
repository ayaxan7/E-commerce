package com.ayaan.bazaar.ui.product.myuploads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.bazaar.domain.model.Product
import com.ayaan.bazaar.domain.repository.ProductRepository
import com.ayaan.bazaar.util.Result
import com.ayaan.bazaar.util.UiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyUploadsViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState.asStateFlow()

    fun loadMyProducts() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    productRepository.getUserProducts(currentUser.uid).collect { products ->
                        _uiState.value = UiState.Success(products)
                    }
                } else {
                    _uiState.value = UiState.Error("User not authenticated")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load your products")
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            when (val result = productRepository.deleteProduct(productId)) {
                is Result.Success -> {
                    _deleteState.value = UiState.Success(Unit)
                    // Refresh the product list after successful deletion
                    loadMyProducts()
                }
                is Result.Error -> {
                    _deleteState.value = UiState.Error(result.exception.message ?: "Failed to delete product")
                }
            }
        }
    }

    fun clearDeleteState() {
        _deleteState.value = UiState.Idle
    }
}
