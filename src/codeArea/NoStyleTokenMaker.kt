package codeArea

import org.fife.ui.rsyntaxtextarea.*
import javax.swing.Action
import javax.swing.text.Segment

class NoStyleTokenMaker : TokenMaker {

    private var firstToken: TokenImpl? = null
    private var currentToken: TokenImpl? = null
    private var previousToken: TokenImpl? = null

    override fun addNullToken() {
        if (firstToken == null) {
            firstToken = TokenImpl()
            currentToken = firstToken
        } else {
            val next = TokenImpl()
            currentToken!!.nextToken = next
            previousToken = currentToken
            currentToken = next
        }
    }

    override fun addToken(array: CharArray?, start: Int, end: Int, tokenType: Int, startOffset: Int) {
        val newToken = TokenImpl(array!!, start, end, startOffset, tokenType, 0)
        if(firstToken == null) {
            firstToken = newToken
            currentToken = firstToken
        } else {
            currentToken!!.nextToken = newToken
            previousToken = currentToken
            currentToken = newToken
        }
    }

    override fun getClosestStandardTokenTypeForInternalType(type: Int): Int = type

    override fun getCurlyBracesDenoteCodeBlocks(languageIndex: Int) = false

    override fun getLastTokenTypeOnLine(text: Segment?, initialTokenType: Int): Int {
        // Last parameter doesn't matter if we're not painting.
        var t = getTokenList(text, initialTokenType, 0)

        while (t.nextToken != null) {
            t = t.nextToken
        }

        return t.type

    }

    override fun getLineCommentStartAndEnd(languageIndex: Int): Array<String>? = null

    override fun getInsertBreakAction(): Action? = null

    override fun getMarkOccurrencesOfTokenType(type: Int) = false

    override fun getOccurrenceMarker(): OccurrenceMarker? = null

    override fun getShouldIndentNextLineAfter(token: Token?) = false

    override fun getTokenList(text: Segment?, initialTokenType: Int, startOffset: Int): Token {
        firstToken = null

        text ?: throw IllegalArgumentException()
        val array = text.array
        val offset = text.offset
        val count = text.count
        val end = offset + count

        // Token starting offsets are always of the form:
        // 'startOffset + (currentTokenStart-offset)', but since startOffset and
        // offset are constant, tokens' starting positions become:
        // 'newStartOffset+currentTokenStart'.
        val newStartOffset = startOffset - offset;

//        var numStart: Int? = null
//        for (i in offset until end) {
//            if(numStart != null && !array[i].isDigit()) {
//                addToken(array, numStart, i-1, Token.COMMENT_EOL, newStartOffset + numStart)
//                currentToken!!.isHyperlink = true
//                numStart = null
//            }
//            when(array[i]) {
//                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
//                    numStart = numStart ?: i
//                    continue
//                }
//                '<', '>' -> addToken(array, i, i, Token.IDENTIFIER, newStartOffset + i)
//                '+', '-' -> addToken(array, i, i, Token.RESERVED_WORD, newStartOffset + i)
//                ',' -> addToken(array, i, i, Token.RESERVED_WORD_2, newStartOffset + i)
//                '.' -> addToken(array, i, i, Token.FUNCTION, newStartOffset + i)
//                '[', ']' -> addToken(array, i, i, Token.ANNOTATION, newStartOffset + i)
//                else -> addToken(array, i, i, Token.COMMENT_EOL, newStartOffset + i)
//            }
////            if(array[i].isDigit()) currentToken!!.isHyperlink = true
//        }
//        if(numStart != null) {
//            addToken(array, numStart, end-1, Token.COMMENT_EOL, newStartOffset + numStart)
//            currentToken!!.isHyperlink = true
//        }

        addToken(array, offset, end-1, Token.IDENTIFIER, newStartOffset)
        addNullToken()
        return firstToken!!
    }

    override fun isIdentifierChar(languageIndex: Int, ch: Char): Boolean {
        return Character.isLetterOrDigit(ch) || ch == '_' || ch == '$'

    }

    override fun isMarkupLanguage() = false
}

fun registerNoStyleLang() {
    val c: Class<*> = NoStyleTokenMaker::class.java
    val atmf = TokenMakerFactory.getDefaultInstance() as AbstractTokenMakerFactory
    atmf.putMapping("text/nostyle", c.name)

}
