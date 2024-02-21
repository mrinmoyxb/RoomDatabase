package com.example.roomdatabase.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomdatabase.Model.Note
import com.example.roomdatabase.Model.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(
    private val dao: NoteDao
): ViewModel(){

    private var isSortedByDateAdded = MutableStateFlow(true)
    private var notes = isSortedByDateAdded.flatMapLatest { sort->
        if(sort){
            dao.getNotesOrderedByDate()
        } else{
            dao.getNoteOrderedByTitle()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())



    val _state: MutableStateFlow<NoteState> = MutableStateFlow(NoteState())
    val state = combine(_state, isSortedByDateAdded, notes){ state, isSortedByDateAdded, notes ->
            state.copy(
                notes = notes
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteState())



    fun onEvent(event: NoteEvents){
        when(event){
            is NoteEvents.deleteNote -> {
                viewModelScope.launch {
                    dao.deleteNote(event.note)
                }
            }

            is NoteEvents.saveNote -> {
                viewModelScope.launch {
                    val note = Note(
                            title = state.value.title.value,
                            description = state.value.description.value,
                            dateAdded = System.currentTimeMillis()
                        )

                    viewModelScope.launch {
                        dao.upsertNote(note)
                    }

                    _state.update{
                        it.copy(
                            title = mutableStateOf(""),
                            description = mutableStateOf("")
                        )
                    }
                }
            }
            NoteEvents.sortNote -> {
                isSortedByDateAdded.value = !isSortedByDateAdded.value

            }
        }
    }

}