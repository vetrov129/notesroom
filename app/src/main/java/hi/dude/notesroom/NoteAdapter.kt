package hi.dude.notesroom

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hi.dude.notesroom.activities.EditorActivity
import hi.dude.notesroom.activities.NotesListActivity
import hi.dude.notesroom.data.Note
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NoteAdapter(
    private val noteList: ArrayList<Note>,
    private val activity: NotesListActivity
) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(activity)
    private val holderList: ArrayList<ViewHolder> = ArrayList()

    var longTextMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.list_item_note, parent, false)
        val viewHolder = ViewHolder(view)
        holderList.add(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = noteList[position]

        if (longTextMode) bindLongMode(holder, note)
        else bindShortMode(holder, note)

        val sdf = SimpleDateFormat("HH:mm dd MMMM yyyy", Locale.getDefault())
        holder.date.text = sdf.format(Date(note.date))

        holder.layout.setBackgroundResource(if (note.isSelected) R.color.colorSelected else R.color.colorLightPrimary)

        holder.itemView.setOnClickListener { clickActionPerformed(position, holder) }
        holder.itemView.setOnLongClickListener { longClickActionPerformed(position, holder) }
    }

    private fun bindLongMode(holder: ViewHolder, note: Note) {
        holder.title.text = note.title
        holder.content.text = note.content

    }

    private fun bindShortMode(holder: ViewHolder, note: Note) {
        if (note.title.length > 16)
            holder.title.text = note.title.substring(0, 11) + "..."
        else
            holder.title.text = note.title

        if (note.content.length > 16)
            holder.content.text = note.content.substring(0, 15) + "..."
        else
            holder.content.text = note.content
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    private fun clickActionPerformed(index: Int, holder: ViewHolder) {
        if (noteList.any { it.isSelected }) {
            longClickActionPerformed(index, holder)
            return
        }
        val intent = Intent(activity, EditorActivity::class.java)
        intent.putExtra("content", Triple(noteList[index].title, noteList[index].content, noteList[index].id))
        intent.putExtra("isUpdate", true)
        activity.startActivity(intent)
    }

    private fun longClickActionPerformed(index: Int, holder: ViewHolder): Boolean {
        val note = noteList[index]

        note.isSelected = !note.isSelected
        holder.layout.setBackgroundResource(if (note.isSelected) R.color.colorSelected else R.color.colorLightPrimary)

        activity.setButtonsMode(noteList.any { it.isSelected })
        return true
    }

    fun setDefaultColor() {
        holderList.forEach { it.layout.setBackgroundResource(R.color.colorLightPrimary) }
        noteList.forEach { it.isSelected = false }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view.findViewById(R.id.item_list_layout)
        val title: TextView = view.findViewById(R.id.title_note_lin)
        val content: TextView = view.findViewById(R.id.content_note_lin)
        val date: TextView = view.findViewById(R.id.date_note_lin)
    }
}