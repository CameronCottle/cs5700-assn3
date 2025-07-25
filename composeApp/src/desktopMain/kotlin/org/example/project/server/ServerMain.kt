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
                        <h1>Shipment Command Submitter</h1>
                        <form id="commandForm">
                            <input type="text" id="commandInput" placeholder="e.g., created,s1,bulk,0" style="width:300px;" />
                            <button type="submit">Submit</button>
                        </form>
                        <p id="responseText"></p>

                        <script>
                            const form = document.getElementById('commandForm');
                            form.addEventListener('submit', async (event) => {
                                event.preventDefault();
                                const input = document.getElementById('commandInput').value;
                                const res = await fetch('/update', {
                                    method: 'POST',
                                    body: input
                                });
                                const text = await res.text();
                                document.getElementById('responseText').textContent = text;
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
                val success = TrackingServer.applyUpdateFromString(updateString)
                if (success) {
                    call.respondText("Update applied", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Unknown update type", status = HttpStatusCode.BadRequest)
                }
            } catch (e: Exception) {
                call.respondText("Error parsing update: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        // Track a shipment
        get("/track/{id}") {
            val id = call.parameters["id"]
            val shipment = id?.let { TrackingServer.findShipment(it) }

            if (shipment == null) {
                call.respondText("Shipment not found", status = HttpStatusCode.NotFound)
            } else {
                call.respondText(shipment.toString(), status = HttpStatusCode.OK)
            }
        }
    }
}
