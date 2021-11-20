package codeArea

import org.fife.ui.rsyntaxtextarea.*
import javax.swing.Action
import javax.swing.text.Segment

class MemoryTokenParser : TokenMaker {

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

        var space = 0
        while(space < array.size && array[space] != ' ' && array[space] != '\u00A0') space++

        for (i in offset until end step (space + 1)) {
            val type = when {
                array[i + space] == '\u00A0' -> Token.ERROR_CHAR
                array.sliceArray(i until (i + space)).all { it == '0' } -> Token.COMMENT_EOL
                else -> Token.IDENTIFIER
            }
            addToken(array, i, i + space, type, newStartOffset + i)
        }

        addNullToken()
        return firstToken!!
    }

    override fun isIdentifierChar(languageIndex: Int, ch: Char) = Character.isDigit(ch)

    override fun isMarkupLanguage() = false
}

fun registerMemoryLang() {
    val c: Class<*> = MemoryTokenParser::class.java
    val atmf = TokenMakerFactory.getDefaultInstance() as AbstractTokenMakerFactory
    atmf.putMapping("text/memory", c.name)

}
