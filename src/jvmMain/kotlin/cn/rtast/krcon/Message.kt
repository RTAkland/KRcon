/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/20 01:24
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krcon

public data class Message(
    val length: Int,
    val id: Int,
    val type: MessageType,
    val body: String
) {
    override fun toString(): String {
        return this.body
    }
}