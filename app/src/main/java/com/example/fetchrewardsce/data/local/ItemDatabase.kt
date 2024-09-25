package com.example.fetchrewardsce.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ItemDatabase: RoomDatabase() {

    abstract val itemDao: ItemDAO

    companion object{

        @Volatile
        private var instance: ItemDatabase? = null

        operator fun invoke(context: Context) = instance ?: synchronized(this){
            Room.databaseBuilder(
                context = context.applicationContext,
                klass = ItemDatabase::class.java,
                name = "fetch_item_db",
            ).build().also { instance = it }
        }
    }

}