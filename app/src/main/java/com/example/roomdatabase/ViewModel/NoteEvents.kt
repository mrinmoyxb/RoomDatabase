package com.example.roomdatabase.ViewModel

import com.example.roomdatabase.Model.Note

sealed class NoteEvents {
    object sortNote: NoteEvents()
    data class deleteNote(val note: Note): NoteEvents()

    data class saveNote(val title: String, val description: String): NoteEvents()
}