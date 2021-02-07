package hi.dude.notesroom.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hi.dude.notesroom.App
import hi.dude.notesroom.NoteAdapter
import hi.dude.notesroom.R
import hi.dude.notesroom.data.Note
import hi.dude.notesroom.data.NoteDao
import java.util.concurrent.atomic.AtomicBoolean

class NotesListActivity : AppCompatActivity() {

    private lateinit var notes: ArrayList<Note>
    private lateinit var rv: RecyclerView
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnCancel: FloatingActionButton
    private lateinit var btnDelete: FloatingActionButton
    private lateinit var adapter: NoteAdapter
    private lateinit var btnTextMode: ImageButton
    private var anySelectedButtonsMode = true
    private var fullSizeTextMode = false

    private lateinit var noteDao: NoteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)


        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar_note_list)

        noteDao = (application as App).getNoteDao()

        rv = findViewById(R.id.recycler_nla)
        btnAdd = findViewById(R.id.button_add_nla)
        btnCancel = findViewById(R.id.button_cancel_nla)
        btnDelete = findViewById(R.id.button_delete_nla)
        btnTextMode = findViewById(R.id.image_button_text_mode)

        btnAdd.setOnClickListener { addClicked() }
        btnDelete.setOnClickListener { deleteClicked() }
        btnCancel.setOnClickListener { cancelClicked() }
        btnTextMode.setOnClickListener { textModeClicked() }

    }

    override fun onResume() {
        rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        setAdapter()
        setButtonsMode(false)
        super.onResume()
    }

    private fun setAdapter() {
        initNotes()

        adapter = NoteAdapter(notes, this)
        rv.adapter = adapter
    }

    private fun initNotes() {
        val thread = Thread {
            notes = ArrayList(noteDao.getAllNotes())
        }

        thread.start()
        thread.join(3000)
        notes.sortByDescending { it.date }
    }

    private fun addClicked() {
        startActivity(Intent(this, EditorActivity::class.java))
    }

    fun setButtonsMode(isAnySelected: Boolean) {
        if (anySelectedButtonsMode == isAnySelected)
            return

        anySelectedButtonsMode = isAnySelected
        if (isAnySelected) {
            btnAdd.hide()
            btnCancel.show()
            btnDelete.show()
        } else {
            btnAdd.show()
            btnCancel.hide()
            btnDelete.hide()
        }
    }

    private fun cancelClicked() {
        setButtonsMode(false)
        adapter.setDefaultColor()
    }

    private fun deleteClicked() {
        val atom = AtomicBoolean(false)
        val thread = Thread {
            try {
                notes.forEach { if (it.isSelected) noteDao.delete(it) }
                atom.set(true)
            } catch (e: Exception) {
                e.printStackTrace()
                atom.set(false)
            }
        }

        thread.start()
        thread.join(3000)
        if (atom.get()) {
            notes.removeIf { it.isSelected }
            setButtonsMode(false)
            adapter.notifyDataSetChanged()
        } else
            Toast.makeText(this, "Проблемы с соединением", Toast.LENGTH_SHORT).show()
    }

    private fun textModeClicked() {
        if (fullSizeTextMode) {
            btnTextMode.setBackgroundResource(R.color.colorTransparent)
        } else {
            btnTextMode.setBackgroundResource(R.color.colorButtonEnabled)
        }
        fullSizeTextMode = !fullSizeTextMode

        adapter.longTextMode = !adapter.longTextMode
        adapter.notifyDataSetChanged()
    }

}