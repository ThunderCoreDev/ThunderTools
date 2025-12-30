package com.maritza.thundertools.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

object SoapClient {
    private val client = OkHttpClient()

    fun execute(
        emulator: String,
        host: String,
        port: String,
        user: String,
        pass: String,
        commandKey: String,
        args: Map<String, String> = emptyMap()
    ): Result<String> {
        val url = "http://$host:$port/"
        val action = soapActionFor(emulator, commandKey)
        val envelope = buildEnvelope(emulator, user, pass, action, args)

        val media = "text/xml; charset=utf-8".toMediaType()
        val body = RequestBody.create(media, envelope)
        val req = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("SOAPAction", action)
            .build()

        return try {
            client.newCall(req).execute().use { resp ->
                Result.success(resp.body?.string().orEmpty())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun soapActionFor(emulator: String, commandKey: String): String {
        // Placeholder: cada emulador puede tener acciones SOAP distintas
        return "urn:$emulator:$commandKey"
    }

    private fun buildEnvelope(
        emulator: String, user: String, pass: String,
        action: String, args: Map<String, String>
    ): String {
        val argsXml = args.entries.joinToString("") { "<${it.key}>${it.value}</${it.key}>" }
        return """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
              <soapenv:Body>
                <command emulator="$emulator" action="$action">
                  <auth>
                    <user>$user</user>
                    <pass>$pass</pass>
                  </auth>
                  $argsXml
                </command>
              </soapenv:Body>
            </soapenv:Envelope>
        """.trimIndent()
    }
}