package com.jonnydev.bmp.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg items: T)
    
    @Update
    fun update(item: T)
    
    @Update
    fun update(vararg items: T)
    
    @Delete
    fun delete(item: T)
}
