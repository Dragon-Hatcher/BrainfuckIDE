package codeArea

import ideaDarkTheme
import jetbrainsMono
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.folding.FoldManager
import org.fife.ui.rtextarea.LineNumberList
import org.fife.ui.rtextarea.RTextArea
import org.fife.ui.rtextarea.RTextScrollPane
import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import javax.swing.JPanel

class BFCodeArea : JPanel() {
    val codeArea = RSyntaxTextArea(10, 50)
    val scrollArea = RTextScrollPane(codeArea)

    init {
        codeArea.syntaxEditingStyle = "text/brainfuck"
        codeArea.isCodeFoldingEnabled = true
        codeArea.isBracketMatchingEnabled = true
        ideaDarkTheme.apply(codeArea)
        codeArea.font = jetbrainsMono.deriveFont(13f)
        codeArea.syntaxScheme.styles.forEach{it.font = jetbrainsMono.deriveFont(13f)}
        codeArea.hyperlinksEnabled = true
        codeArea.showMatchedBracketPopup = true

        this.layout = BorderLayout()
        this.add(scrollArea, BorderLayout.CENTER)
    }


}

/*
Á Ă Â Ä À Ā Ą Å Ã Æ Ǽ Ć Č Ç Ĉ Ċ Ð Ď Đ É Ĕ Ě Ê Ë Ė È Ē Ę Ğ Ǧ Ĝ Ġ Ħ Ĥ Í Ĭ Î Ï İ Ì Ī Į Ĩ Ĵ Ĺ Ľ Ŀ Ł Ń Ň Ŋ Ñ Ó Ŏ Ô Ö Ò Ơ Ő Ō Ø Ǿ Õ Œ Þ Ŕ Ř Ś Š Ş Ŝ ẞ Ə Ŧ Ť Ú Ŭ Û Ü Ù Ư Ű Ū Ų Ů Ũ Ẃ Ŵ Ẅ Ẁ Ý Ŷ Ÿ Ỳ Ź Ž Ż Ģ Ķ Ļ Ņ Ŗ Ţ Ǫ Ǵ Ș Ț Ạ Ả Ấ Ầ Ẩ Ẫ Ậ Ắ Ằ Ẳ Ẵ Ặ Ẹ Ẻ Ẽ Ế Ề Ể Ễ Ệ Ỉ Ị Ọ Ỏ Ố Ồ Ổ Ỗ Ộ Ớ Ờ Ở Ỡ Ợ Ụ Ủ Ứ Ừ Ử Ữ Ự Ỵ Ỷ Ỹ á ă â ä à ā ą å ã æ ǽ ć č ç ĉ ċ ð ď đ é ĕ ě ê ë ė è ē ę ə ğ ǧ ĝ ġ ħ ĥ i ı í ĭ î ï ì ī į ĩ j ȷ ĵ ĸ l ĺ ľ ŀ ł m n ń ŉ ň ŋ ñ ó ŏ ô ö ò ơ ő ō ø ǿ õ œ þ ŕ ř s ś š ş ŝ ß ſ ŧ ť ú ŭ û ü ù ư ű ū ģ ķ ļ ņ ŗ ţ ǫ ǵ ș ț ạ ả ấ ầ ẩ ẫ ậ ắ ằ ẳ ẵ ặ ẹ ẻ ẽ ế ề ể ễ ệ ỉ ị ọ ỏ ố ồ ổ ỗ ộ ớ ờ ở ỡ ợ ụ ủ ứ ừ ử ữ ự ỵ ỷ ỹ ų ů ũ ẃ ŵ ẅ ẁ ý ŷ ÿ ỳ z ź ž ż
 */