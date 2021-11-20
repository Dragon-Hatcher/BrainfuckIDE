package extensions

import java.awt.Component
import java.awt.event.KeyListener
import javax.swing.JComponent

fun JComponent.recursivelyAddKeyListener(l: KeyListener) {
    this.addKeyListener(l)
    this.components.forEach {
        if(it is JComponent) {
            it.recursivelyAddKeyListener(l)
        } else {
            it.addKeyListener(l)
        }
    }
}