package com.marchenaya.mypomodoro

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform