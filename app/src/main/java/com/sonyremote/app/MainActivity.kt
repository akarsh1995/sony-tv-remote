package com.sonyremote.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sonyremote.app.ui.RemoteScreen
import com.sonyremote.app.ui.theme.RemoteChassisDark
import com.sonyremote.app.ui.theme.SonyRemoteTheme
import com.sonyremote.app.ui.viewmodel.RemoteViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RemoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SonyRemoteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RemoteChassisDark
                ) {
                    RemoteScreen(viewModel = viewModel)
                }
            }
        }
    }
}
