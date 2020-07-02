package skyrim.requiem.gui

import skyproc.gui.HyperManager
import skyrim.requiem.fptools.None
import skyrim.requiem.fptools.Option
import skyrim.requiem.fptools.Some
import skyrim.requiem.localization.TextFormatter
import skyrim.requiem.localization.TextReference
import skyrim.requiem.localization.Translatable
import java.awt.Color
import javax.swing.ImageIcon
import javax.swing.JEditorPane
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.ToolTipManager

class PopupTools(private val formatter: TextFormatter) {

    private val backgroundColor: Color
    private val style: String

    init {
        val template = JLabel()
        style = """
            font-family: ${template.font};
            font-weight: normal;
            font-size: ${template.font.size}pt;
            width: 600px;
        """.trimIndent()
        backgroundColor = template.background
    }

    fun showPopupMessage(title: TextReference, message: TextReference, messageType: PopupType) {
        val toDisplay = """<html><body style="$style">${formatter.format(message)}</body></html>"""
        val content = JEditorPane("text/html", toDisplay)
        ToolTipManager.sharedInstance().registerComponent(content)
        content.isEditable = false
        content.addHyperlinkListener(HyperManager(content))
        content.background = backgroundColor
        JOptionPane.showMessageDialog(null, content, formatter.format(title), messageType.swingType, messageType.icon)
    }

    @Deprecated("use the version with TextReference arguments instead")
    fun showPopupMessage(title: String, message: String, messageType: PopupType) {
        val toDisplay = """<html><body style="$style">$message</body></html>"""
        val content = JEditorPane("text/html", toDisplay)
        ToolTipManager.sharedInstance().registerComponent(content)
        content.isEditable = false
        content.addHyperlinkListener(HyperManager(content))
        content.background = backgroundColor
        JOptionPane.showMessageDialog(null, content, title, messageType.swingType, messageType.icon)
    }

    /**
     * returns an empty option if the user closes the popup instead of answering the question
     */
    fun <E> showPopupQuestion(
        title: TextReference,
        message: TextReference,
        options: Array<E>,
        initial: E,
        messageType: PopupType
    ): Option<E> where E : Enum<E>, E : Translatable {
        val toDisplay = """<html><body style="$style">${formatter.format(message)}</body></html>"""
        val content = JEditorPane("text/html", toDisplay)
        ToolTipManager.sharedInstance().registerComponent(content)
        content.isEditable = false
        content.addHyperlinkListener(HyperManager(content))
        content.background = backgroundColor

        val choice = JOptionPane.showOptionDialog(
            null,
            content,
            formatter.format(title),
            JOptionPane.OK_CANCEL_OPTION,
            messageType.swingType,
            messageType.icon,
            options.map { formatter.format(it.text) }.toTypedArray(),
            formatter.format(initial.text)
        )
        return if (choice >= 0) Some(options[choice]) else None()
    }

    companion object {
        private fun loadImage(path: String): ImageIcon = ImageIcon(this::class.java.getResource(path))

        enum class PopupType(val icon: ImageIcon, val swingType: Int) {
            Warning(loadImage("/auxiliaryfiles/warning.png"), JOptionPane.WARNING_MESSAGE),
            Error(loadImage("/auxiliaryfiles/error.png"), JOptionPane.ERROR_MESSAGE),
            PatchSuccess(loadImage("/auxiliaryfiles/success.png"), JOptionPane.INFORMATION_MESSAGE)
        }
    }
}