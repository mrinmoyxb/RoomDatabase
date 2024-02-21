package com.example.roomdatabase.Model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Upsert
    suspend fun upsertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT *FROM note_table ORDER BY dateAdded")
    fun getNotesOrderedByDate(): Flow<List<Note>>

    @Query("SELECT *FROM note_table ORDER BY title")
    fun getNoteOrderedByTitle():Flow<List<Note>>

}