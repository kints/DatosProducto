package mx.tecmilenio.prueba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.tecmilenio.prueba.data.productoRepository
import mx.tecmilenio.prueba.ui.theme.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "lista_productos") {
                        composable("lista_productos") {
                            ListaProductos(
                                navController = navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable(
                            "detalle_producto/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.IntType })
                        ) {
                            backStackEntry ->
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
        }
    }
}

@Composable
fun ListaProductos(navController: NavController, modifier: Modifier = Modifier) {
    val newRepository = productoRepository()
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        )
    )
    LazyColumn {
        items(newRepository.listaProductos){
            producto ->
            Row(modifier = Modifier.background(Color.LightGray.copy(alpha=alpha)).clickable { navController.navigate("detalle_producto/${producto.id}") }) {
                Column {
                    Image(
                        painter = painterResource(R.drawable.producto),
                        contentDescription = producto.nombre,
                        modifier = modifier.size(width = 30.dp, height = 30.dp)
                    )
                }
                Column {
                    Text(
                        text = producto.nombre,
                        modifier = modifier,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = producto.descripcion,
                        modifier = modifier,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        val navController = rememberNavController()
        ListaProductos(navController)
    }
}
