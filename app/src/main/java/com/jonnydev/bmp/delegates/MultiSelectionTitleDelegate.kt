package com.jonnydev.bmp.delegates

import androidx.appcompat.view.ActionMode
import kotlin.reflect.KProperty

class MultiSelectionTitleDelegate(private val actionMode: ActionMode?) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): CharSequence? {
        return actionMode?.title
    }
    
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: CharSequence?) {
        actionMode?.title = value
    }
}
