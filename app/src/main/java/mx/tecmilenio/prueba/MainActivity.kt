package mx.tecmilenio.prueba

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.getSystemService
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.tecmilenio.prueba.data.productoRepository
import mx.tecmilenio.prueba.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                PruebaApp(widthSizeClass = windowSizeClass.widthSizeClass)
            }
        }
    }
}

@Composable
fun PruebaApp(widthSizeClass: WindowWidthSizeClass) {
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            CompactLayout()
        }
        WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
            MediumAndExpandedLayout()
        }
    }
}

@Composable
fun CompactLayout() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "lista_productos") {
            composable("lista_productos") {
                ListaProductos(
                    modifier = Modifier.padding(innerPadding),
                    onProductClick = { productId ->
                        navController.navigate("detalle_producto/$productId")
                    }
                )
            }
            composable(
                "detalle_producto/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId")
                if (productId != null) {
                    DetalleProductoScreen(
                        productId = productId,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MediumAndExpandedLayout() {
    var selectedProductId by rememberSaveable { mutableStateOf<Int?>(null) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ListaProductos(
                modifier = Modifier.weight(1f),
                onProductClick = { productId ->
                    selectedProductId = productId
                }
            )
            if (selectedProductId != null) {
                DetalleProductoScreen(
                    productId = selectedProductId!!,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Select a product to see details")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListaProductos(modifier: Modifier = Modifier, onProductClick: (Int) -> Unit) {
    val newRepository = remember { productoRepository() }
    val productList = remember { mutableStateListOf(*newRepository.listaProductos.toTypedArray()) }
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        )
    )
    // 1. Obtener el contexto actual
    val context = LocalContext.current

    // 2. Obtener el VibratorManager del contexto
    val vibratorManager = context.getSystemService(VibratorManager::class.java)
    val vibrator = vibratorManager.defaultVibrator
    LazyColumn(modifier = modifier) {
        items(items = productList, key = { it.id }) { producto ->
            AnimatedVisibility(visible = !producto.completado, exit = fadeOut() + scaleOut()) {
                Row(
                    modifier = Modifier
                        .background(Color.LightGray.copy(alpha = alpha))
                        .clickable {
                            val vibratorEffect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                            vibrator.vibrate(vibratorEffect)
                            onProductClick(producto.id) }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { change, dragAmount ->
                                val index = productList.indexOf(producto)
                                if (index != -1) {
                                    if (dragAmount > 0) {
                                        productList[index] =
                                            productList[index].copy(completado = true)
                                    } else if (dragAmount < 0) {
                                        productList.removeAt(index)

                                    }
                                }
                            }
                        }
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "",
                            tint = if (producto.completado) Color.Green else Color.Red
                        )
                    }
                    Column {
                        Text(
                            text = producto.nombre,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = producto.descripcion,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        ListaProductos(onProductClick = {})
    }
}
