package mx.tecmilenio.prueba

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import mx.tecmilenio.prueba.data.productoRepository
import mx.tecmilenio.prueba.ui.theme.AppTheme

@Composable
fun DetalleProductoScreen(productId: Int, modifier: Modifier = Modifier) {
//    val context = LocalContext.current
//    val options = GestureRecognizer.GestureRecognizerOptions.builder()
//        .setBaseOptions(BaseOptions.builder().setModelAssetPath("gesture_recognizer.task").build())
//        .build()
//    val recognizer = GestureRecognizer.createFromOptions(context, options)
//
//    recognizer.recognize(videoFrame).addOnSuccessListener { recognizedGestures ->
//        val gesture = result.gesture().firstOrNull()?.first()?.categoryName()
//        updateUI(gesture)
//    }
        val product = productoRepository().listaProductos.find { it.id == productId }
        if (product != null) {
            Row(modifier = modifier.clickable(
                onClick = {},
                interactionSource = remember {
                    MutableInteractionSource() },
                indication = ripple() )
            ) {
                Column {
                    Image(
                        painter = painterResource(R.drawable.producto),
                        contentDescription = product.nombre,
                        modifier = Modifier.size(width = 50.dp, height = 50.dp)
                    )
                }
                Column {
                    Text(text = product.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(text = product.descripcion, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            Text("Producto no encontrado")
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DetalleProductoScreenPreview() {
        AppTheme {
            DetalleProductoScreen(1)
        }
    }