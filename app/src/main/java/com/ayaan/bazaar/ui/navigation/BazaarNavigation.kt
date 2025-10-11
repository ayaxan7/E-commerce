package com.ayaan.bazaar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ayaan.bazaar.ui.auth.AuthScreen
import com.ayaan.bazaar.ui.auth.AuthViewModel
import com.ayaan.bazaar.ui.product.createproduct.CreateProductScreen
import com.ayaan.bazaar.ui.product.productdetails.ProductDetailScreen
import com.ayaan.bazaar.ui.product.productlist.ProductListScreen
import com.ayaan.bazaar.ui.product.myuploads.MyUploadsScreen
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.compose.koinViewModel

@Composable
fun BazaarNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val auth= FirebaseAuth.getInstance()
    val user=auth.currentUser
    val startDestination = if (user != null) {
        Screen.ProductList.route
    } else {
        Screen.Auth.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProductList.route) {
            ProductListScreen(
                onNavigateToCreateProduct = {
                    navController.navigate(Screen.CreateProduct.route)
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.ProductList.route) { inclusive = true }
                    }
                },
                onNavigateToMyUploads = {
                    navController.navigate(Screen.MyUploads.route)
                }
            )
        }

        composable(Screen.CreateProduct.route) {
            CreateProductScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProductList = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.CreateProduct.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = Screen.ProductDetail.arguments
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.MyUploads.route) {
            MyUploadsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object ProductList : Screen("product_list")
    object CreateProduct : Screen("create_product")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
        val arguments = listOf(
            androidx.navigation.navArgument("productId") {
                type = androidx.navigation.NavType.StringType
            }
        )
    }
    object MyUploads : Screen("my_uploads")
}
