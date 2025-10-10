package com.cpiekarski.fourteeners

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cpiekarski.fourteeners.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
private fun HomeScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Summit Register", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = {
            // Open 14ers.com app/site for logging a route
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.14ers.com"))
            context.startActivity(intent)
        }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Log Summit Attempt")
        }
        Button(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.14ers.com/php14ers/13ers.php?type=14ers"))
            context.startActivity(intent)
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text("View History")
        }
    }
}
