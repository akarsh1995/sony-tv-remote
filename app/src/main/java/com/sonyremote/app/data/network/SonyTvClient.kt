package com.sonyremote.app.data.network

import com.sonyremote.app.data.model.IrccCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Sony TV HTTP Client for both IRCC-IP (SOAP) and REST API (JSON-RPC).
 */
class SonyTvClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .build()
) {

    private val xmlMediaType = "text/xml; charset=UTF-8".toMediaType()
    private val jsonMediaType = "application/json; charset=UTF-8".toMediaType()

    /**
     * Sends an IRCC remote command to the Sony TV using the SOAP XML protocol.
     */
    suspend fun sendIrccCommand(
        ipAddress: String,
        psk: String,
        command: IrccCommand
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val soapEnvelope = """
                <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                    <s:Body>
                        <u:X_SendIRCC xmlns:u="urn:schemas-sony-com:service:IRCC:1">
                            <IRCCCode>${command.code}</IRCCCode>
                        </u:X_SendIRCC>
                    </s:Body>
                </s:Envelope>
            """.trimIndent()

            val requestBody = soapEnvelope.toRequestBody(xmlMediaType)
            val request = Request.Builder()
                .url("http://$ipAddress/sony/ircc")
                .header("SOAPACTION", "\"urn:schemas-sony-com:service:IRCC:1#X_SendIRCC\"")
                .header("X-Auth-PSK", psk)
                .header("Content-Type", "text/xml; charset=UTF-8")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("IRCC Error: HTTP ${response.code} - ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Checks if TV is reachable and retrieves power status.
     */
    suspend fun checkPowerStatus(
        ipAddress: String,
        psk: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("method", "getPowerStatus")
                put("params", JSONArray())
                put("id", 1)
                put("version", "1.0")
            }.toString()

            val requestBody = payload.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url("http://$ipAddress/sony/system")
                .header("X-Auth-PSK", psk)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP ${response.code}"))
                }
                val bodyStr = response.body?.string() ?: ""
                val json = JSONObject(bodyStr)
                if (json.has("result")) {
                    val resultArr = json.getJSONArray("result")
                    if (resultArr.length() > 0) {
                        val statusObj = resultArr.getJSONObject(0)
                        val status = statusObj.optString("status", "")
                        return@withContext Result.success(status.equals("active", ignoreCase = true))
                    }
                }
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sets the power status using Sony REST API (/sony/system).
     */
    suspend fun setPowerStatus(
        ipAddress: String,
        psk: String,
        powerOn: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val statusParam = JSONObject().apply {
                put("status", powerOn)
            }
            val payload = JSONObject().apply {
                put("method", "setPowerStatus")
                put("params", JSONArray().put(statusParam))
                put("id", 1)
                put("version", "1.0")
            }.toString()

            val requestBody = payload.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url("http://$ipAddress/sony/system")
                .header("X-Auth-PSK", psk)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Power command failed: HTTP ${response.code}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves system information (MAC address, model) from TV.
     */
    suspend fun getSystemInfo(
        ipAddress: String,
        psk: String
    ): Result<Map<String, String>> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("method", "getSystemInformation")
                put("params", JSONArray())
                put("id", 1)
                put("version", "1.0")
            }.toString()

            val requestBody = payload.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url("http://$ipAddress/sony/system")
                .header("X-Auth-PSK", psk)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP ${response.code}"))
                }
                val bodyStr = response.body?.string() ?: ""
                val json = JSONObject(bodyStr)
                val infoMap = mutableMapOf<String, String>()
                if (json.has("result")) {
                    val resultArr = json.getJSONArray("result")
                    if (resultArr.length() > 0) {
                        val infoObj = resultArr.getJSONObject(0)
                        infoMap["model"] = infoObj.optString("model", "BRAVIA")
                        infoMap["macAddr"] = infoObj.optString("macAddr", "")
                        infoMap["product"] = infoObj.optString("product", "TV")
                    }
                }
                Result.success(infoMap)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
