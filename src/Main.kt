import codeArea.registerBFLang
import codeArea.registerFoldParser
import codeArea.registerMemoryLang
import com.formdev.flatlaf.FlatDarculaLaf
import interpreter.Dialect
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.Theme
import uiComponents.BFMenuBar
import uiComponents.BFScreen
import uiComponents.settings.SettingScreen
import java.awt.Font
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.SwingUtilities

val resFolder = File("res")

val ideaDarkTheme: Theme = Theme.load(File(resFolder, "bfideadark.xml").inputStream())
val jetbrainsMono: Font =
    Font.createFont(Font.TRUETYPE_FONT, File(resFolder, "JetBrainsMono-Regular.ttf")).deriveFont(14.0f)
val logoSmall: Image = ImageIO.read(File(resFolder, "logo@x16.png"))
val logoLarge: Image = ImageIO.read(File(resFolder, "logo@x32.png"))

var doBreaks = true

/*
TODO: Toggle between hex, dec, and char memory view
TODO: Prompt to save before closing
TODO: Figure out line numbers for memory
 */

lateinit var settings: SettingScreen

fun main() {
//    println(RSyntaxTextArea().javaClass.declaredFields.map { it.name }.forEach(::println))

    SwingUtilities.invokeLater {
        FlatDarculaLaf.install()

        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)

        registerBFLang()
        registerFoldParser()
        registerMemoryLang()
//        registerMemFoldParser()

        settings = SettingScreen()
        settings.dialectsSettingScreen.dialect = Dialect(
            null, Dialect.CellSizes.EIGHT, Dialect.EOFActions.ZERO, true,
            printInput = true,
            breakChar = null
        )

        val window = JFrame()
        val menu = BFMenuBar()
        val screen = BFScreen(menu)
        window.iconImages = listOf(logoSmall, logoLarge)
        window.title = "Neuron IDE"
        window.jMenuBar = menu
        window.add(screen)
        window.pack()
        window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        window.isVisible = true
        settings.isVisible = true
    }
}

/*
Dialect Options:
 - Cell Size: 8 bit, 16 bit, Unlimited?     | v/
 - Wrapping: wrap, no wrap                  | v/
 - EOF character: no change, 0, -1          | v/
 - Memory Size: 30,000, custom              | v/
 - print input to console                   | v/
 - error when printing beyond ascii         |
 - allow leftward memory                    |
 - eol comment marker                       |
 - memory wraps                             |

Editor Options:
 - font size
 - animate brackets
 - fold code around

Refactoring
 - Indent block
 - Enforce 80 char limit
 - export without comments

 */
