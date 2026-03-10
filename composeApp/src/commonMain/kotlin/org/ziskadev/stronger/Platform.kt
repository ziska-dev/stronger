package org.ziskadev.stronger

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform