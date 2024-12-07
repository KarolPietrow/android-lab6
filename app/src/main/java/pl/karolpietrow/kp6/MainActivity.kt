package pl.karolpietrow.kp6

import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.karolpietrow.kp6.ui.theme.KP6Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KP6Theme {
                val viewModel: MyViewModel = viewModel()
                MainScreen(viewModel)
            }
        }
    }
}

fun checkNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

@Composable
fun MainScreen(viewModel: MyViewModel) {
    val books by viewModel.books.collectAsState()
    val context = LocalContext.current
    val bookServiceIntent = Intent(context, BookService::class.java)
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var permissionGranted by remember { mutableStateOf(checkNotificationPermission(context)) }



    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (!isGranted) {
            Toast.makeText(
                context,
                "Brak zezwolenia ! Nie można wyświetlić powiadomienia.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    DisposableEffect(Unit) {
        val receiver = MyBroadcastReceiver { newData ->
            newData.forEach { book ->
                viewModel.addBook(book) {
                    Toast.makeText(context, "Książka o tytule \"${book.title}\" już istnieje!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val intentFilter = IntentFilter("pl.karolpietrow.DATA_DOWNLOADED")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(receiver, intentFilter)
        }

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Moje książki",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "ID",
                modifier = Modifier.weight(0.1f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tytuł książki",
                modifier = Modifier.weight(0.75f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Liczba słów",
                modifier = Modifier.weight(0.25f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Liczba liter",
                modifier = Modifier.weight(0.25f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Najczęstsze słowo",
                modifier = Modifier.weight(0.25f),
                fontWeight = FontWeight.Bold
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .weight(1f)
        ) {
            items(books) {
                book -> Row(
                    modifier = Modifier
                        .clickable { selectedBook = book }
                ) {
                Text(text = "${book.id}", modifier = Modifier.weight(0.1f))
                Text(text = book.title, modifier = Modifier.weight(0.75f))
                Text(text = "${book.wordCount}", modifier = Modifier.weight(0.25f))
                Text(text = "${book.charCount}", modifier = Modifier.weight(0.25f))
                Text(text = book.mostCommonWord, modifier = Modifier.weight(0.25f))
            }
            }
        }
        Row {
            Button(
                onClick = {
                    if (permissionGranted) {
                        context.startService(bookServiceIntent)
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                },
                modifier = Modifier.padding(5.dp)
            ) {
                Text("Pobierz losową książkę")
            }
            Button(
                onClick = {
                    viewModel.clearList()
                },
                modifier = Modifier.padding(5.dp)
            ) {
                Text("Wyczyść listę")
            }
        }
    }
    if (selectedBook != null) {
        AlertDialog(
            onDismissRequest = { selectedBook = null },
            confirmButton = {
                Button(onClick = { selectedBook = null } ) {
                    Text("Zamknij")
                }
            },
            title = {
                Text(text = selectedBook!!.title + " - opis")
            },
            text = {
                Column (
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ){
                    Text(text = selectedBook!!.content)
                }
            }
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    KP6Theme {
//        MainScreen()
//    }
//}