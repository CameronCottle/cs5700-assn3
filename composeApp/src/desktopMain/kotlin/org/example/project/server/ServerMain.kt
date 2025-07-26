package org.example.project.server

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.request.*
import org.example.project.server.TrackingServer

fun Application.module() {
    routing {
        // Serve form UI
        get("/") {
            call.respondText(
                """
                <html>
                    <body style="background-color:black; color:white; font-family:sans-serif; padding:2rem;">
                        <h1>Shipment Server</h1>
                        <form id="commandForm">
                            <input type="text" id="commandInput" placeholder="e.g., created,s1,bulk,0" style="width:300px;" />
                            <button type="submit">Submit</button>
                        </form>
                        <p id="responseText"></p>

                        <script>
                            const form = document.getElementById('commandForm');
                            const inputField = document.getElementById('commandInput');
                            const responseText = document.getElementById('responseText');
                        
                            form.addEventListener('submit', async (event) => {
                                event.preventDefault();
                                const input = inputField.value;
                        
                                try {
                                    const res = await fetch('/update', {
                                        method: 'POST',
                                        body: input
                                    });
                        
                                    // Clear the input field always
                                    inputField.value = "";
                        
                                    // Only show message if there's an error
                                    if (!res.ok) {
                                        const text = await res.text();
                                        responseText.textContent = text;
                                        responseText.style.color = "red";
                                    } else {
                                        responseText.textContent = ""; // Clear any old error
                                    }
                                } catch (e) {
                                    responseText.textContent = "Network error: " + e.message;
                                    responseText.style.color = "red";
                                }
                            });
                        </script>
                    </body>
                </html>
                """.trimIndent(),
                ContentType.Text.Html
            )
        }

        // Receive update command
        post("/update") {
            val updateString = call.receiveText()

            try {
                val success = TrackingServer.applyShippingUpdate(updateString)
                if (success) {
                    call.respondText("Update applied", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Unknown update type", status = HttpStatusCode.BadRequest)
                }
            } catch (e: Exception) {
                call.respondText("Error parsing update: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }
    }
}
