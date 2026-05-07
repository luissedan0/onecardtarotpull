package com.luissedan0.onecardtarotpull.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(): RoomDatabase.Builder<TarotDatabase> {
    val dbPath = NSHomeDirectory() + "/Documents/tarot.db"
    return Room.databaseBuilder<TarotDatabase>(name = dbPath)
}
