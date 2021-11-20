package extensions

import javax.swing.JComponent

fun <T: JComponent> T.withTTT(tooltip: String): T {
    this.toolTipText = tooltip
    return this
}