package com.ayaan.bazaar.ui.product.createproduct

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayaan.bazaar.domain.model.ProductCategory
import com.ayaan.bazaar.domain.model.ProductCondition
import com.ayaan.bazaar.ui.common.ErrorMessage
import com.ayaan.bazaar.ui.common.GradientButton
import com.ayaan.bazaar.ui.common.LoadingIndicator
import com.ayaan.bazaar.ui.product.createproduct.components.AddImageButton
import com.ayaan.bazaar.ui.product.createproduct.components.ImagePreview
import com.ayaan.bazaar.util.UiState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProductList: () -> Unit,
    viewModel: CreateProductViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedImages by viewModel.selectedImages.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ProductCategory.ELECTRONICS) }
    var mrp by remember { mutableStateOf("") }
    var askingPrice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var selectedCondition by remember { mutableStateOf(ProductCondition.GOOD) }

    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showConditionDropdown by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.addImages(uris)
    }

    // Handle successful product creation
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            onNavigateToProductList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Product") }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Image Selection Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Product Images (Min 3 required)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                AddImageButton(
                                    onClick = { imagePickerLauncher.launch("image/*") })
                            }

                            items(selectedImages) { uri ->
                                ImagePreview(
                                    uri = uri, onRemove = { viewModel.removeImage(uri) })
                            }
                        }

                        Text(
                            text = "${selectedImages.size} images selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product Details Form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Product Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Category Dropdown
                        ExposedDropdownMenuBox(
                            expanded = showCategoryDropdown,
                            onExpandedChange = { showCategoryDropdown = it }) {
                            OutlinedTextField(
                                value = selectedCategory.displayName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = showCategoryDropdown,
                                onDismissRequest = { showCategoryDropdown = false }) {
                                ProductCategory.values().forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.displayName) },
                                        onClick = {
                                            selectedCategory = category
                                            showCategoryDropdown = false
                                        })
                                }
                            }
                        }

                        // Price Fields
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = mrp,
                                onValueChange = { mrp = it },
                                label = { Text("MRP (₹)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = askingPrice,
                                onValueChange = { askingPrice = it },
                                label = { Text("Asking Price (₹)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }

                        // Description
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 4
                        )

                        // City and Year
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = year,
                                onValueChange = { year = it },
                                label = { Text("Year") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }

                        // Condition Dropdown
                        ExposedDropdownMenuBox(
                            expanded = showConditionDropdown,
                            onExpandedChange = { showConditionDropdown = it }) {
                            OutlinedTextField(
                                value = selectedCondition.displayName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Condition") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showConditionDropdown) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = showConditionDropdown,
                                onDismissRequest = { showConditionDropdown = false }) {
                                ProductCondition.values().forEach { condition ->
                                    DropdownMenuItem(
                                        text = { Text(condition.displayName) },
                                        onClick = {
                                            selectedCondition = condition
                                            showConditionDropdown = false
                                        })
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                GradientButton(
                    text = "Create Product", onClick = {
                        viewModel.createProduct(
                            title = title,
                            category = selectedCategory,
                            mrp = mrp,
                            askingPrice = askingPrice,
                            description = description,
                            city = city,
                            year = year,
                            condition = selectedCondition
                        )
                    }, enabled = uiState !is UiState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Loading overlay
            if (uiState is UiState.Loading) {
                LoadingIndicator()
            }

            // Error message
            if (uiState is UiState.Error) {
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    ErrorMessage(
                        message = (uiState as UiState.Error).message,
                        onRetry = { viewModel.clearError() })
                }
            }
        }
    }
}