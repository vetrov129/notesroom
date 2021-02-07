package hi.dude.notesroom.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hi.dude.notesroom.App
import hi.dude.notesroom.R
import hi.dude.notesroom.data.Note
import hi.dude.notesroom.data.NoteDao
import hi.dude.notesroom.ext.toEditable
import java.lang.StringBuilder
import java.util.concurrent.atomic.AtomicBoolean

class EditorActivity : AppCompatActivity() {

    private lateinit var btSave: FloatingActionButton
    private lateinit var edNote: EditText
    private lateinit var edTitle: EditText
    private var isUpdate = false
    private var id = -1

    private lateinit var noteDao: NoteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        noteDao = (application as App).getNoteDao()

        isUpdate = intent.getBooleanExtra("isUpdate", false)

        btSave = findViewById(R.id.button_ok_nna)
        edTitle = findViewById(R.id.edit_title_nna)
        edNote = findViewById(R.id.edit_note_nna)

        btSave.setOnClickListener { if (isUpdate) saveUpdateClicked() else saveClicked() }

        if (isUpdate) {
            val content = intent.getSerializableExtra("content") as Triple<String, String, Int>
            edTitle.text = content.first.toEditable()
            edNote.text = content.second.toEditable()
            id = content.third
        }
    }

    private fun saveClicked() {
        if (checkFields())
            return
        if (!createNote(getText()))
            return
        finish()
    }

    private fun saveUpdateClicked() {
        val atom = AtomicBoolean(false)
        val title = edTitle.text.toString()
        val content = edNote.text.toString()
        val thread = Thread {
            try {
                noteDao.update(Note(id, title, content, System.currentTimeMillis()))
                atom.set(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
        thread.join(3000)

        if (atom.get()) finish()
        else Toast.makeText(this, "Ошибка при сохранении", Toast.LENGTH_SHORT).show()
    }

    private fun checkFields(): Boolean {
        return if (edNote.text.toString() == "" && edTitle.text.toString() == "") {
            Toast.makeText(this, "Заполните хотя бы одно поле", Toast.LENGTH_SHORT).show()
            true
        } else false
    }

    private fun getText(): Pair<String, String> {
        return if (edTitle.text.toString() == "")
            Pair(fillTitle(edNote.text.toString()), edNote.text.toString())
        else
            Pair(edTitle.text.toString(), edNote.text.toString())
    }

    private fun fillTitle(note: String): String {
        if (note.indexOfFirst { it == '\n' } in 1..30)
            return note.substring(0, note.indexOfFirst { it == '\n' })
        val words = note.split(Regex(" "))
        val builder = StringBuilder()
        for (word in words) {
            builder.append(word).append(" ")
            if (builder.length >= 30) break
        }
        return builder.toString()
    }

    private fun createNote(pair: Pair<String, String>): Boolean {
        val atom = AtomicBoolean(false)
        val thread = Thread {
            try {
                noteDao.insert(Note(0, pair.first, pair.second, System.currentTimeMillis()))
                atom.set(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
        thread.join(3000)
        if (!atom.get())
            Toast.makeText(this, "Ошибка при сохранении", Toast.LENGTH_SHORT).show()
        return atom.get()
    }
}