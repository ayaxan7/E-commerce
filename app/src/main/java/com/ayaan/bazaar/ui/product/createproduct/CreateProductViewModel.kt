package com.ayaan.bazaar.ui.product.createproduct

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.bazaar.domain.model.Product
import com.ayaan.bazaar.domain.model.ProductCategory
import com.ayaan.bazaar.domain.model.ProductCondition
import com.ayaan.bazaar.domain.repository.ProductRepository
import com.ayaan.bazaar.util.Result
import com.ayaan.bazaar.util.UiState
import com.ayaan.bazaar.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateProductViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uiState: StateFlow<UiState<String>> = _uiState.asStateFlow()

    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

    fun addImages(uris: List<Uri>) {
        val currentImages = _selectedImages.value.toMutableList()
        currentImages.addAll(uris)
        _selectedImages.value = currentImages
    }

    fun removeImage(uri: Uri) {
        _selectedImages.value = _selectedImages.value.filter { it != uri }
    }

    fun createProduct(
        title: String,
        category: ProductCategory,
        mrp: String,
        askingPrice: String,
        description: String,
        city: String,
        year: String,
        condition: ProductCondition,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        // Validate inputs
        val titleError = Validators.validateTitle(title)
        val mrpError = Validators.validatePrice(mrp)
        val askingPriceError = Validators.validatePrice(askingPrice)
        val descriptionError = Validators.validateDescription(description)
        val cityError = Validators.validateCity(city)
        val yearError = Validators.validateYear(year)

        when {
            titleError != null -> {
                _uiState.value = UiState.Error(titleError)
                return
            }
            mrpError != null -> {
                _uiState.value = UiState.Error(mrpError)
                return
            }
            askingPriceError != null -> {
                _uiState.value = UiState.Error(askingPriceError)
                return
            }
            descriptionError != null -> {
                _uiState.value = UiState.Error(descriptionError)
                return
            }
            cityError != null -> {
                _uiState.value = UiState.Error(cityError)
                return
            }
            yearError != null -> {
                _uiState.value = UiState.Error(yearError)
                return
            }
            _selectedImages.value.size < 3 -> {
                _uiState.value = UiState.Error("Please select at least 3 images")
                return
            }
        }

        val product = Product(
            title = title,
            category = category.displayName,
            mrp = mrp.toDouble(),
            askingPrice = askingPrice.toDouble(),
            description = description,
            city = city,
            year = year.toInt(),
            condition = condition.displayName,
            latitude = latitude,
            longitude = longitude
        )

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = productRepository.createProduct(product, _selectedImages.value)) {
                is Result.Success -> {
                    _uiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(result.exception.message ?: "Failed to create product")
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = UiState.Idle
    }
}
