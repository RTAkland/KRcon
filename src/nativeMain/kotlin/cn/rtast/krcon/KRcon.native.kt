/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 19:54
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krcon

import kotlinx.cinterop.*
import platform.posix.*

public actual class KRcon actual constructor(host: String, port: Int, password: String?) {
    public constructor(host: String, port: Int) : this(host, port, null)

    private fun buildRconRequest(request: RConRequest): ByteArray {
        val length = 10 + request.body.length
        val byteArray = ByteArray(length)
        var offset = 0
        byteArray[offset++] = (length shr 24).toByte()
        byteArray[offset++] = (length shr 16).toByte()
        byteArray[offset++] = (length shr 8).toByte()
        byteArray[offset++] = length.toByte()
        byteArray[offset++] = (request.id shr 24).toByte()
        byteArray[offset++] = (request.id shr 16).toByte()
        byteArray[offset++] = (request.id shr 8).toByte()
        byteArray[offset++] = request.id.toByte()
        byteArray[offset++] = (request.type shr 24).toByte()
        byteArray[offset++] = (request.type shr 16).toByte()
        byteArray[offset++] = (request.type shr 8).toByte()
        byteArray[offset++] = request.type.toByte()
        for (i in request.body.indices) {
            byteArray[offset++] = request.body[i].code.toByte()
        }
        return byteArray
    }

    @OptIn(ExperimentalForeignApi::class)
    public fun sendMessage(socket: Int, request: RConRequest): ByteArray {
        val requestData = buildRconRequest(request)
        if (send(socket, requestData, requestData.size.toUInt(), 0u) < 0) {
            println("Error sending data.")
            return ByteArray(0)
        }
        val buffer = ByteArray(4096)
        val bytesRead = recv(socket, buffer.refTo(0), buffer.size.toUInt(), 0u)
        if (bytesRead < 0) {
            println("Error reading from socket.")
            return ByteArray(0)
        }

        return buffer.copyOf(bytesRead.toInt())
    }

    public fun close() {

    }
}