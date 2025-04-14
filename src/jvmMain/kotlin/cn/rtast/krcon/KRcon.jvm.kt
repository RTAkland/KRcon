/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 19:54
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krcon

public actual class KRcon actual constructor(host: String, port: Int, password: String?) {
    public constructor(host: String, port: Int) : this(host, port, null)
}