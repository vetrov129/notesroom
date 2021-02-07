package hi.dude.notesroom.ext

import android.text.Editable

fun String.toEditable(): Editable? = Editable.Factory.getInstance().newEditable(this)