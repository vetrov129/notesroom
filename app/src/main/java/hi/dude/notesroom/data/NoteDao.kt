package hi.dude.notesroom.data

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes;")
    fun getAllNotes(): List<Note>

    @Delete
    fun delete(note: Note)

    @Update
    fun update(note: Note)

    @Insert
    fun insert(note: Note)
}