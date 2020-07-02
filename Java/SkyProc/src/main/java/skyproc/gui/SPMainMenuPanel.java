/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import lev.gui.*;
import skyproc.SPGlobal;

/**
 * A GUI panel that functions as the main menu in SUMGUI setups, and links to
 * several SPSettingPanel instantiations.
 *
 * @author Justin Swanson
 */
public class SPMainMenuPanel extends JPanel {

    static final int spacing = 35;
    static final int xPlacement = SUMGUI.leftDimensions.width - 25;
    int yPlacement = 0;
    LImagePane customLogo;
    LLabel version;
    Color color;
    /**
     * Reference to the left column main menu panel.
     */
    protected LScrollPane menuScroll;
    /**
     *
     */
    protected LPanel menuPanel = new LPanel(SUMGUI.leftDimensions);
    ArrayList<SPSettingPanel> panels = new ArrayList<>();
    SPSettingPanel welcome = null;
    SPSettingPanel activePanel;

    /**
     * Creates a new Main Menu with the desired menu color.
     *
     * @param menuColor
     */
    public SPMainMenuPanel(Color menuColor) {
	this.setLayout(null);
	setSize(SUMGUI.middleLeftDimensions.getSize());
	setLocation(0, 0);
	int topMargin = 170;
	int bottomMargin = 40;
	menuPanel.setPreferredSize(menuPanel.getSize());
	menuScroll = new LScrollPane(menuPanel);
	menuScroll.setLocation(0, topMargin);
	menuScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	menuScroll.setSize(SUMGUI.leftDimensions.width, SUMGUI.leftDimensions.height - topMargin - bottomMargin);
	add(menuScroll);
	setOpaque(false);
	color = menuColor;
    }

    /**
     * Creates a new Main Menu with the default menu color.
     */
    public SPMainMenuPanel() {
	this(SUMGUI.light);
    }

    /**
     * Adds the desired logo to the top portion of the main menu.
     *
     * @param logo
     */
    public void addLogo(URL logo) {
	try {
	    int height = 150;
	    customLogo = new LImagePane(logo);
	    customLogo.setMaxSize(SUMGUI.leftDimensions.width, height);
	    customLogo.setLocation(SUMGUI.leftDimensions.width / 2 - customLogo.getWidth() / 2, 14);
	    add(customLogo);
	    tieWelcomeAndLogo();
	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	}
    }

    /**
     * Adds the version number just under the logo's position.
     *
     * @param version
     */
    public void setVersion(String version) {
	setVersion(version, new Point(5, 5));
    }

    /**
     * Adds the version number at the desired location.
     *
     * @param version
     * @param location
     */
    public void setVersion(String version, Point location) {
	if (!hasVersion()) {
	    this.version = new LLabel(version, new Font("Serif", Font.PLAIN, 10), SUMGUI.darkGray);
	    add(this.version);
	}
	this.version.setLocation(location);
    }

    /**
     *
     * @return True if version display has already been created.
     */
    public boolean hasVersion() {
	return version != null;
    }

    /**
     * Hooks together a SPSettingPanel to the main menu, and adds a GUI listing
     * on the main menu.
     *
     * @param panel Panel to add to the main menu
     * @param checkBoxPresent
     * @param save Save to tie to.
     * @param setting Setting to tie to.
     * @return The main menu GUI component
     */
    public SPMainMenuConfig addMenu(SPSettingPanel panel, boolean checkBoxPresent, LSaveFile save, Enum setting) {
	return addMenu(panel, color, checkBoxPresent, save, setting);
    }

    /**
     * Hooks together a SPSettingPanel to the main menu, and adds a GUI listing
     * on the main menu.
     *
     * @param panel Panel to add to the main menu
     * @param c Color to make the menu text
     * @param checkBoxPresent
     * @param save Save to tie to.
     * @param setting Setting to tie to.
     * @return The main menu GUI component
     */
    public SPMainMenuConfig addMenu(SPSettingPanel panel, Color c, boolean checkBoxPresent, LSaveFile save, Enum setting) {
	SPMainMenuConfig menuConfig = new SPMainMenuConfig(panel.header, checkBoxPresent, c, new Point(xPlacement, yPlacement), save, setting);
	yPlacement += spacing;
	menuConfig.addActionListener(panel.getOpenHandler());
	menuPanel.add(menuConfig);
	menuPanel.setPreferredSize(new Dimension(menuPanel.getPreferredSize().width, yPlacement));
	return menuConfig;
    }

    /**
     * Hooks together a SPSettingPanel to the main menu, and adds a GUI listing
     * on the main menu.
     *
     * @param panel Panel to add to the main menu
     * @return The main menu GUI component
     */
    public SPMainMenuConfig addMenu(SPSettingPanel panel) {
	return addMenu(panel, false, null, null);
    }

    /**
     * Opens and displays a panel.
     *
     * @param panel
     */
    public void openPanel(SPSettingPanel panel) {
	if (activePanel != null) {
	    activePanel.close(this);
	}
	int index = panels.indexOf(panel);
	if (index != -1) {
	    activePanel = panels.get(index);
	} else {
	    panels.add(panel);
	    activePanel = panel;
	    add(panel);
	}
	activePanel.setVisible(true);
    }

    /**
     * Sets a panel to be displayed upon opening the program.
     *
     * @param panel
     */
    public void setWelcomePanel(SPSettingPanel panel) {
	welcome = panel;
	welcome.open();
	tieWelcomeAndLogo();
    }

    void tieWelcomeAndLogo() {
	if (welcome != null && customLogo != null) {
	    customLogo.addMouseListener(new MouseListener() {
		@Override
		public void mouseClicked(MouseEvent e) {
		    welcome.open();
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	    });
	}
    }

    /**
     *
     * @param backgroundPicture
     */
    public void setBackgroundPicture(URL backgroundPicture) {
	SUMGUI.setBackgroundPicture(backgroundPicture);
    }

    /**
     *
     * @param font
     * @param helpSize
     * @param headerSize
     * @param menuSize
     */
    public void setMainFont(Font font, int helpSize, int headerSize, int menuSize) {
	SUMGUI.helpPanel.setHeaderFont(font.deriveFont(Font.PLAIN, helpSize));
	SUMGUI.SUMmainFont = font.deriveFont(Font.PLAIN, headerSize);
	SPMainMenuConfig.size = menuSize;
    }

    /**
     *
     * @param fontURL
     * @param helpSize
     * @param headerSize
     * @param menuSize
     */
    public void setMainFont(URL fontURL, int helpSize, int headerSize, int menuSize) {
	try {
	    Font font = Font.createFont(Font.TRUETYPE_FONT, fontURL.openStream());
	    setMainFont(font, helpSize, headerSize, menuSize);
	} catch (IOException | FontFormatException ex) {
	    SPGlobal.logException(ex);
	}
    }

    void open() {
	setVisible(true);
	repaint();
    }
}
