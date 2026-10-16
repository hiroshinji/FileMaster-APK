package com.example.filemaster

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StatFs
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { FileMasterApp() } }
}

@Composable
private fun FileMasterApp() {
    val context = LocalContext.current
    var dark by remember { mutableStateOf(false) }
    var showHidden by remember { mutableStateOf(false) }
    var rootUri by remember { mutableStateOf<Uri?>(null) }
    var currentUri by remember { mutableStateOf<Uri?>(null) }
    var refresh by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("Escolha uma pasta para começar. O Android solicitará autorização de acesso.") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            try { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION) } catch (_: Exception) {}
            rootUri = uri; currentUri = uri; message = "Pasta aberta"; refresh++
        }
    }
    MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
        Scaffold { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text("FileMaster", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Gerenciador de arquivos", style = MaterialTheme.typography.bodyMedium)
                    }
                    TextButton(onClick = { dark = !dark }) { Text(if (dark) "☀ Claro" else "☾ Escuro") }
                }
                Spacer(Modifier.height(14.dp))
                StorageCard()
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { picker.launch(null) }) { Text("Abrir pasta") }
                    Spacer(Modifier.width(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = showHidden, onCheckedChange = { showHidden = it; refresh++ })
                        Text("Mostrar ocultos")
                    }
                }
                Text(message, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                if (currentUri != null) {
                    val folder = remember(currentUri, refresh) { DocumentFile.fromTreeUri(context, currentUri!!) }
                    val entries = remember(folder, showHidden, refresh) {
                        (folder?.listFiles()?.toList() ?: emptyList()).filter { showHidden || !(it.name ?: "").startsWith(".") }.sortedWith(compareByDescending<DocumentFile> { it.isDirectory }.thenBy { (it.name ?: "").lowercase() })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Arquivos", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        TextButton(onClick = { currentUri = rootUri; refresh++ }, enabled = currentUri != rootUri) { Text("Início") }
                    }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(entries, key = { it.uri.toString() }) { item ->
                            Row(Modifier.fillMaxWidth().clickable {
                                if (item.isDirectory) { currentUri = item.uri; refresh++ }
                                else {
                                    val intent = Intent(Intent.ACTION_VIEW).apply { setDataAndType(item.uri, context.contentResolver.getType(item.uri) ?: "*/*"); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
                                    try { context.startActivity(Intent.createChooser(intent, "Abrir arquivo com")) } catch (_: Exception) { message = "Nenhum aplicativo compatível encontrado." }
                                }
                            }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(if (item.isDirectory) "📁" else "📄", modifier = Modifier.width(34.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(item.name ?: "(sem nome)", maxLines = 1)
                                    Text(if (item.isDirectory) "Pasta" else formatSize(item.length()), style = MaterialTheme.typography.bodySmall)
                                }
                                TextButton(onClick = { message = "Selecione a ação de arquivo: copiar/mover/lixeira será implementado na próxima etapa." }) { Text("⋮") }
                            }
                            HorizontalDivider()
                        }
                    }
                } else {
                    Spacer(Modifier.height(24.dp))
                    Text("Acesso controlado pelo Android", style = MaterialTheme.typography.titleMedium)
                    Text("Use ‘Abrir pasta’ para escolher uma pasta. Por segurança, versões recentes do Android restringem o acesso direto a Android/data, Android/obb e áreas privadas de outros aplicativos.")
                }
            }
        }
    }
}

@Composable
private fun StorageCard() {
    val context = LocalContext.current
    val stats = remember {
        try {
            val path = android.os.Environment.getDataDirectory().path
            val fs = StatFs(path)
            val total = fs.totalBytes; val free = fs.availableBytes
            Triple(total, free, total - free)
        } catch (_: Exception) { Triple(0L, 0L, 0L) }
    }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Armazenamento interno", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text("${formatSize(stats.third)} usados · ${formatSize(stats.second)} livres")
            LinearProgressIndicator(progress = { if (stats.first > 0) stats.third.toFloat() / stats.first else 0f }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
            Text("Total: ${formatSize(stats.first)}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

private fun formatSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = listOf("KB", "MB", "GB", "TB")
    var value = bytes.toDouble(); var index = -1
    do { value /= 1024.0; index++ } while (value >= 1024 && index < units.lastIndex)
    return String.format(java.util.Locale.getDefault(), "%.1f %s", value, units[index])
}
