package com.example.DangerBook.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.DangerBook.data.local.storage.UserPreferences
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//funcion para guardar la foto capturada por la cámara
private fun createTempImageFile(context: Context): File{
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir,"images").apply {
        if(!exists()) mkdirs() //crea la carpeta si no existe
    }
    return File(storageDir,"IMG_$timeStamp.jpg") //archivo temporal jpg
}

//convertir la uri de la imagen mediante el FileProvider
private fun getImageUriFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context,authority,file)
}

@Composable // Pantalla Home (sin formularios, solo navegación/diseño)
fun HomeScreen(
    onGoLogin: () -> Unit,   // Acción a Login
    onGoRegister: () -> Unit // Acción a Registro
) {
    //contexto actual (necesario para el FileProvider)
    val context = LocalContext.current

    //Recepciono la informacion del Data Store que necesito
    val userPrefs = remember { UserPreferences(context) }
    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(false)

    //guardar la ultima foto tomada
    var photoUriString by rememberSaveable { mutableStateOf<String?>(null) }
    //para almacenar la Uri actual para mostrarla en  la UI
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    //launcher para abrir la camara y guardar la foto en el archivo temporal
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if(success){
            //si se abrio correctamente la camara,guardamos la Uri de la foto
            photoUriString = pendingCaptureUri?.toString()
            Toast.makeText(context,"Foto guardada correctamente",Toast.LENGTH_SHORT).show()
        }else{
            //si se cancela o no se toma la foto
            pendingCaptureUri = null
            Toast.makeText(context,"No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }

    }

    val bg = MaterialTheme.colorScheme.surfaceVariant // Fondo agradable para Home

    Box( // Contenedor a pantalla completa
        modifier = Modifier
            .fillMaxSize() // Ocupa todo
            .background(bg) // Aplica fondo
            .padding(16.dp), // Margen interior
        contentAlignment = Alignment.Center // Centra contenido
    ) {
        Column( // Estructura vertical
            horizontalAlignment = Alignment.CenterHorizontally // Centra hijos
        ) {
            // Cabecera como Row (ejemplo de estructura)
            Row(
                verticalAlignment = Alignment.CenterVertically // Centra vertical
            ) {
                Text( // Título Home
                    text = "Home",
                    style = MaterialTheme.typography.headlineSmall, // Estilo título
                    fontWeight = FontWeight.SemiBold // Seminegrita
                )
                Spacer(Modifier.width(8.dp)) // Separación horizontal
                //Icono que depende de si esta logueado o no
                Icon(
                    imageVector = if(isLoggedIn) Icons.Filled.Person else Icons.Filled.PersonOff,
                    contentDescription = if(isLoggedIn) "Usuario Contectado" else "Usuario no conectado",
                    tint = if(isLoggedIn) MaterialTheme.colorScheme.primary else
                    MaterialTheme.colorScheme.outline
                )
                AssistChip( // Chip decorativo (Material 3)
                    onClick = {}, // Sin acción (demo)
                    label = { Text("Navega desde arriba o aquí") } // Texto chip
                )
            }

            Spacer(Modifier.height(20.dp)) // Separación

            // Tarjeta con un mini “hero”
            ElevatedCard( // Card elevada para remarcar contenido
                modifier = Modifier.fillMaxWidth() // Ancho completo
            ) {
                Column(
                    modifier = Modifier.padding(16.dp), // Margen interno de la card
                    horizontalAlignment = Alignment.CenterHorizontally // Centrado
                ) {
                    Text(
                        "Demostración de navegación con TopBar + Drawer + Botones",
                        style = MaterialTheme.typography.titleMedium, // Estilo medio
                        textAlign = TextAlign.Center // Alineación centrada
                    )
                    Spacer(Modifier.height(12.dp)) // Separación
                    Text(
                        "Usa la barra superior (íconos y menú), el menú lateral o estos botones.",
                        style = MaterialTheme.typography.bodyMedium // Texto base
                    )
                }
            }

            Spacer(Modifier.height(24.dp)) // Separación

            //nuevo elevatedCard para la cámara
            ElevatedCard (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ){
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Captura de foto con cámara del dispositivo",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    if(photoUriString.isNullOrEmpty()){
                        Text(
                            text = "No ha tomado fotos",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                    } else{
                        //si existe una foto se muestra
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(Uri.parse(photoUriString))
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto Tomada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    var showDialog by remember { mutableStateOf(false)}
                    //boton para abrir camara
                    Button(
                        onClick = {
                            val file = createTempImageFile(context)
                            val uri = getImageUriFile(context,file)
                            pendingCaptureUri = uri
                            takePictureLauncher.launch(uri)
                        }
                    ) {
                        Text(
                            if(photoUriString.isNullOrEmpty()) "Abrir Cámara"
                            else "Volver a tomar"
                        )
                    }

                    //boton adicional para eliminar la foto
                    if(!photoUriString.isNullOrEmpty()){
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { showDialog = true }) {
                            Text("Eliminar Foto")
                        }
                    }

                    //dialogo de confirmación
                    if(showDialog){
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Confirmación") },
                            text = { Text("¿Desea eliminar la fotografía?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    photoUriString = null
                                    showDialog = false
                                    Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show()
                                }) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false}) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }
            }
            // Botones de navegación principales
            Row( // Dos botones en fila
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre botones
            ) {
                Button(onClick = onGoLogin) { Text("Ir a Login") } // Navega a Login
                OutlinedButton(onClick = onGoRegister) { Text("Ir a Registro") } // A Registro
            }
        }
    }
}