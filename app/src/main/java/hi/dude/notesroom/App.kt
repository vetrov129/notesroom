package hi.dude.notesroom

import android.app.Application
import androidx.room.Room
import hi.dude.notesroom.data.DaoGetter
import hi.dude.notesroom.data.NoteDao

class App: Application() {

    companion object{
        var userLogin = ""
    }

    private lateinit var daoGetter: DaoGetter

    override fun onCreate() {
        super.onCreate()
        initDao()
    }

    private fun initDao() {
        val thread = Thread {
            daoGetter = Room.databaseBuilder(applicationContext, DaoGetter::class.java, "notes.sqlite").build()
        }

        thread.start()
        thread.join(3000)
    }

    fun getNoteDao(): NoteDao {
        return daoGetter.getNoteDao()
    }
}