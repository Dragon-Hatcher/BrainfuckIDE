package uiComponents

import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem


class BFMenuBar : JMenuBar() {
    val file = JMenu("File")                            //
    val save = JMenuItem("Save")                        //
    val saveAs = JMenuItem("Save As")                   //
    val open = JMenuItem("Open")                        //
    val new = JMenuItem("New")                          //

    val dialect = JMenu("Dialect")                      //
    val changeDialect = JMenuItem("Change Dialect")     //

    val tools = JMenu("Tools")                          //
    val number = JMenuItem("Insert Number Code")        //
    val text = JMenuItem("Insert Text Code")            //

    init {
        //TODO add keys for menus, as well as accelerators

        file.add(save)
        file.add(saveAs)
        file.add(open)
        file.add(new)
        this.add(file)

        dialect.add(changeDialect)
        this.add(dialect)

        tools.add(number)
        tools.add(text)
        this.add(tools)
    }

}