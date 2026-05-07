package com.luissedan0.onecardtarotpull.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import com.luissedan0.onecardtarotpull.AppContextHolder

actual fun getDatabaseBuilder(): RoomDatabase.Builder<TarotDatabase> {
    val context = AppContextHolder.context
    val dbPath = context.getDatabasePath("tarot.db").absolutePath
    return Room.databaseBuilder<TarotDatabase>(
        context = context,
        name = dbPath
    )
}
