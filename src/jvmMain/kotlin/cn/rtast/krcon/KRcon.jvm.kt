/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 19:54
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krcon

import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicInteger

public actual class KRcon actual constructor(host: String, port: Int, password: String?) {
    public constructor(host: String, port: Int) : this(host, port, null)

    private var conn: Socket = Socket(host, port)
    private val lastId = AtomicInteger(0)
    private val headerSize: Int = 10

    private fun parseInt(b: ByteArray): Int {
        var result = 0
        for (i in b.indices) {
            result = result or (b[i].toInt() shl 8 * i)
        }
        return result
    }

    private fun decodeMessage(msg: ByteArray): Message {
        val length = this.parseInt(msg.sliceArray(IntRange(0, 3)))
        val id = this.parseInt(msg.sliceArray(IntRange(4, 7)))
        val type = MessageType.fromInt(this.parseInt(msg.sliceArray(IntRange(8, 11))))
        val body = msg.sliceArray(IntRange(12, length + 1)).decodeToString()
        return Message(length, id, type, body)
    }

    private fun encodeMessage(msg: Message): ByteArray {
        val buf: ByteBuffer = ByteBuffer.allocate(msg.length + 4)
        buf.order(ByteOrder.LITTLE_ENDIAN)
        buf.putInt(msg.length)
        buf.putInt(msg.id)
        buf.putInt(msg.type.value)
        buf.put(msg.body.toByteArray())
        buf.put(byteArrayOf(0, 0))
        buf.flip()

        val bytes = ByteArray(msg.length + 4)
        buf.get(bytes, 0, bytes.size)
        return bytes
    }

    private fun sendMessage(messageType: MessageType, body: String): Message {
        val length = body.length + headerSize
        val id = lastId.addAndGet(1)
        val message = Message(length, id, messageType, body)

        val bytes: ByteArray = this.encodeMessage(message)
        conn.outputStream.write(bytes)
        conn.outputStream.flush()

        val lengthBytes = ByteArray(4)
        conn.inputStream.read(lengthBytes)
        val lengthBuf: ByteBuffer = ByteBuffer.wrap(lengthBytes)
        lengthBuf.order(ByteOrder.LITTLE_ENDIAN)

        val respSize: Int = lengthBuf.getInt()
        lengthBuf.flip()

        val respBuf: ByteBuffer = ByteBuffer.allocate(respSize + 4).put(lengthBuf)
        respBuf.order(ByteOrder.LITTLE_ENDIAN)
        val respBox = ByteArray(respSize)
        conn.inputStream.read(respBox)
        respBuf.put(respBox)
        respBuf.flip()

        val respBytes = ByteArray(respSize + 4)
        respBuf.get(respBytes, 0, respBytes.size)
        return this.decodeMessage(respBytes)
    }

    public fun authenticate(password: String) {
        val response = this.sendMessage(MessageType.AUTHENTICATE, password)
        if (response.id == -1) {
            throw AuthFailedException("Password is not correct!")
        }
    }

    public fun executeCommand(body: String): Message {
        return this.sendMessage(MessageType.COMMAND, body)
    }

    public fun close() {
        conn.close()
    }
}