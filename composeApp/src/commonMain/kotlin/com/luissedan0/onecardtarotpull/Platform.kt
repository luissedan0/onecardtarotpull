package com.luissedan0.onecardtarotpull

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform