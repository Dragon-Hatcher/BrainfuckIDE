package codeArea

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.folding.Fold
import org.fife.ui.rsyntaxtextarea.folding.FoldParser
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager

//class MemoryFoldParser : FoldParser {
//    override fun getFolds(textArea: RSyntaxTextArea?): MutableList<Fold> {
//        textArea ?: return mutableListOf()
//        val folds: MutableList<Fold> = mutableListOf()
//        var i = 0
//        while(i < textArea.text.length) {
//            if(textArea.text[i] == '\n') {
//                folds.add(Fold(0, textArea, i).also {
//                    it.endOffset = i + 15
//                    it.isCollapsed = true
//                })
//                i += 16
//            }
//            i++
//        }
//        return folds
//    }
//}
//
//fun registerMemFoldParser() {
//    FoldParserManager.get().addFoldParserMapping("text/memory", MemoryFoldParser())
//}

class BFFoldParser : FoldParser {
    override fun getFolds(textArea: RSyntaxTextArea?): MutableList<Fold> {
        textArea ?: return mutableListOf()

        val folds: MutableMap<Fold, Int> = mutableMapOf()
        var currentFold: Fold? = null
        var line = 0
        for ((index, char) in textArea.text.withIndex()) {
            if(char == '\n') line++
            when (char) {
                '[' -> {
                    if (currentFold == null) {
                        currentFold = Fold(0, textArea, index)
                        folds[currentFold] = line
                    } else {
                        currentFold = currentFold.createChild(0, index)
                    }
                }
                ']' -> {
                    if (currentFold != null) {
                        currentFold.endOffset = index
                        val oldCurrent = currentFold
                        currentFold = oldCurrent.parent
                        if(line == folds[oldCurrent]) {
                            oldCurrent.removeFromParent()
                            folds.remove(oldCurrent)
                        }
                    }
                }
            }
        }
        return folds.keys.toMutableList()
    }
}

fun registerFoldParser() {
    FoldParserManager.get().addFoldParserMapping("text/brainfuck", BFFoldParser())
}