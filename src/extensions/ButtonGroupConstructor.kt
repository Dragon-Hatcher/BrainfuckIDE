package extensions

import javax.swing.AbstractButton
import javax.swing.ButtonGroup

fun ButtonGroup(vararg buttons: AbstractButton): ButtonGroup {
    val b = ButtonGroup()
    buttons.forEach { b.add(it) }
    return b
}
