package uiComponents.settings

import logoLarge
import logoSmall
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTabbedPane

class SettingScreen : JFrame() {

    val dialectsSettingScreen = DialectsSettingScreen()

    init {
        val settingsPane = JTabbedPane()
        settingsPane.add("Dialect", dialectsSettingScreen)
        settingsPane.add("Settings", JPanel())
        settingsPane.add("Editor", JPanel())

        this.iconImages = listOf(logoSmall, logoLarge)
        this.title = "Neuron IDE Settings"
        this.add(settingsPane)
        this.pack()
        this.defaultCloseOperation = HIDE_ON_CLOSE
    }
}