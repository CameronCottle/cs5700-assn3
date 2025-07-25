package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlin.concurrent.thread
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.project.server.module


fun main() = application {
    // Start Ktor server in background
    thread(start = true, isDaemon = true) {
        embeddedServer(Netty, port = 8080) {
            module()
        }.start(wait = true)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Assn2",
        alwaysOnTop = true,
    ) {
        App()
    }
}
