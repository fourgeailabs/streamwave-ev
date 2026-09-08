package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

data class PlexPinState(
    val id: Long = 0,
    val code: String = "",
    val qrUrl: String? = null,
    val expiresInSeconds: Int = 900,
    val authToken: String? = null,
    val isLinked: Boolean = false,
    val errorMessage: String? = null
)

data class PlexServerInfo(
    val name: String,
    val clientIdentifier: String,
    val connectionUri: String,
    val isLocal: Boolean
)

class PlexAuthService(
    context: Context? = null
) {
    private val clientIdentifier: String = getOrCreateClientId(context)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val TAG = "PlexAuthService"
        private const val PLEX_API_BASE = "https://plex.tv/api/v2"
        private const val PRODUCT_NAME = "StreamWave"
        private const val APP_VERSION = "1.02.00"
        private const val DEVICE_NAME = "Equinox EV"
        private const val PLATFORM_NAME = "Android Automotive"
        private const val PREFS_NAME = "streamwave_auth_prefs"
        private const val KEY_CLIENT_ID = "plex_client_identifier"

        private fun getOrCreateClientId(context: Context?): String {
            if (context != null) {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val existing = prefs.getString(KEY_CLIENT_ID, null)
                if (!existing.isNullOrBlank()) {
                    return existing
                }
                val newId = "streamwave-" + UUID.randomUUID().toString()
                prefs.edit().putString(KEY_CLIENT_ID, newId).apply()
                return newId
            }
            return "streamwave-" + UUID.randomUUID().toString()
        }
    }

    private fun buildStandardHeaders(builder: Request.Builder, token: String? = null): Request.Builder {
        builder.header("X-Plex-Product", PRODUCT_NAME)
            .header("X-Plex-Version", APP_VERSION)
            .header("X-Plex-Client-Identifier", clientIdentifier)
            .header("X-Plex-Device", DEVICE_NAME)
            .header("X-Plex-Platform", PLATFORM_NAME)
            .header("X-Plex-Model", "Chevrolet Equinox EV")
            .header("Accept", "application/json")
        if (!token.isNullOrEmpty()) {
            builder.header("X-Plex-Token", token)
        }
        return builder
    }

    /**
     * Requests a new 4-digit PIN code for TV login from plex.tv.
     * Note: strong=false (or omitting strong) generates the standard 4-character TV PIN code for plex.tv/link.
     */
    suspend fun requestPin(): PlexPinState = withContext(Dispatchers.IO) {
        try {
            val emptyBody = "".toRequestBody("application/json".toMediaType())
            val requestBuilder = Request.Builder()
                .url("$PLEX_API_BASE/pins?strong=false")
                .post(emptyBody)
            buildStandardHeaders(requestBuilder)

            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            val bodyString = response.body?.string()

            if (response.isSuccessful && !bodyString.isNullOrEmpty()) {
                val json = JSONObject(bodyString)
                val id = json.optLong("id")
                val code = json.optString("code")
                val qr = json.optString("qr", null)
                val expires = json.optInt("expiresIn", 900)
                Log.d(TAG, "Acquired real Plex 4-digit TV PIN: $code (ID: $id, QR: $qr)")
                return@withContext PlexPinState(
                    id = id,
                    code = code,
                    qrUrl = qr,
                    expiresInSeconds = expires
                )
            } else {
                Log.w(TAG, "Plex PIN request returned code ${response.code}: $bodyString")
                return@withContext PlexPinState(
                    errorMessage = "Plex returned HTTP ${response.code}. Please check internet or use manual token."
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error contacting Plex API for PIN: ${e.message}")
            return@withContext PlexPinState(
                errorMessage = "Network error connecting to plex.tv: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Polls the PIN status to verify if user approved it on plex.tv/link.
     */
    suspend fun checkPinStatus(pinId: Long, pinCode: String): PlexPinState = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder()
                .url("$PLEX_API_BASE/pins/$pinId?code=$pinCode")
                .get()
            buildStandardHeaders(requestBuilder)

            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            val bodyString = response.body?.string()

            if (response.isSuccessful && !bodyString.isNullOrEmpty()) {
                val json = JSONObject(bodyString)
                val authToken = if (json.isNull("authToken")) null else {
                    val raw = json.optString("authToken", "")
                    if (raw.isNotBlank() && raw != "null") raw else null
                }
                val isLinked = !authToken.isNullOrEmpty()
                return@withContext PlexPinState(
                    id = pinId,
                    code = pinCode,
                    qrUrl = json.optString("qr", null),
                    authToken = authToken,
                    isLinked = isLinked
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking PIN status: ${e.message}")
        }
        return@withContext PlexPinState(id = pinId, code = pinCode, isLinked = false)
    }

    /**
     * Fetches accessible servers from the Plex account resources endpoint.
     */
    suspend fun discoverServers(authToken: String): List<PlexServerInfo> = withContext(Dispatchers.IO) {
        val servers = mutableListOf<PlexServerInfo>()
        try {
            val requestBuilder = Request.Builder()
                .url("$PLEX_API_BASE/resources?includeHttps=1")
                .get()
            buildStandardHeaders(requestBuilder, authToken)

            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            val bodyString = response.body?.string()

            if (response.isSuccessful && !bodyString.isNullOrEmpty()) {
                val trimmed = bodyString.trim()
                val jsonArray: JSONArray = when {
                    trimmed.startsWith("[") -> JSONArray(trimmed)
                    trimmed.startsWith("{") -> {
                        val obj = JSONObject(trimmed)
                        obj.optJSONArray("resources") ?: JSONArray()
                    }
                    else -> JSONArray()
                }

                for (i in 0 until jsonArray.length()) {
                    val serverObj = jsonArray.getJSONObject(i)
                    val provides = serverObj.optString("provides", "")
                    if (provides.contains("server")) {
                        val name = serverObj.optString("name", "Plex Server")
                        val clientIdent = serverObj.optString("clientIdentifier", "")
                        val connections = serverObj.optJSONArray("connections")
                        var chosenUri = ""
                        var isLocal = false
                        if (connections != null && connections.length() > 0) {
                            // Find reachable connection (local or remote https)
                            for (c in 0 until connections.length()) {
                                val conn = connections.getJSONObject(c)
                                val uri = conn.optString("uri", "")
                                val local = conn.optBoolean("local", false)
                                if (uri.isNotEmpty()) {
                                    chosenUri = uri
                                    isLocal = local
                                    if (uri.startsWith("https")) break
                                }
                            }
                        }
                        if (chosenUri.isNotEmpty()) {
                            servers.add(
                                PlexServerInfo(
                                    name = name,
                                    clientIdentifier = clientIdent,
                                    connectionUri = chosenUri,
                                    isLocal = isLocal
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error discovering Plex servers: ${e.message}")
        }

        if (servers.isEmpty()) {
            // Default home server candidate
            servers.add(
                PlexServerInfo(
                    name = "Equinox Home Plex",
                    clientIdentifier = "plex-home-streamwave",
                    connectionUri = "https://plex.local:32400",
                    isLocal = true
                )
            )
        }
        return@withContext servers
    }

    /**
     * Direct test for a user-supplied Plex server URL and token.
     */
    suspend fun verifyDirectServer(serverUrl: String, token: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val normalizedUrl = serverUrl.trimEnd('/')
            val requestBuilder = Request.Builder()
                .url("$normalizedUrl/identity")
                .get()
            buildStandardHeaders(requestBuilder, token)
            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.w(TAG, "Direct server verification failed: ${e.message}")
            return@withContext false
        }
    }
}
