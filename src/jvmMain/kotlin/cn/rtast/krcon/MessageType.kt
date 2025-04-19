/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/20 01:23
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krcon

public enum class MessageType(public val value: Int) {
    RESPONSE(0),
    COMMAND(2),
    AUTHENTICATE(3);

    public companion object {
        private val VALUES = entries.toTypedArray()
        public fun fromInt(value: Int): MessageType = VALUES.first { it.value == value }
    }
}