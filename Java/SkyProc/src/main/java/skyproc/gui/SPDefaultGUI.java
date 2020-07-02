package skyproc.gui;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import lev.gui.LImagePane;
import lev.gui.LLabel;
import lev.gui.LProgressBar;
import lev.gui.LTextPane;
import lev.gui.resources.LFonts;
import lev.gui.resources.LImages;

/**
 * A GUI setup that offered as an easy out-of-the-box GUI option.
 *
 * @author Justin Swanson
 */
public class SPDefaultGUI extends JFrame {

    LImagePane backgroundPanel;
    LImagePane patcherLogo;
    Component descriptionAnchor;
    LLabel pluginLabel;
    LLabel patching;
    LTextPane description;
    LImagePane skyprocLogo;
    LProgressBar pbar;

    /**
     * Creates and displays the SkyProc default GUI.
     *
     * @param yourPatcherName This will be used as the title on the GUI.
     * @param yourDescription This will be displayed under the title.
     */
    public SPDefaultGUI(final String yourPatcherName, final String yourDescription) {
	super(yourPatcherName);
	pbar = new LProgressBar(250, 15, new Font("SansSerif", Font.PLAIN, 11), new Color(180, 180, 180));
	SPProgressBarPlug.addProgressBar(pbar);
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		init(yourPatcherName, yourDescription);
	    }
	});

    }

    final void init(String pluginName, String descriptionText) {
	try {
	    // Set up frame
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setSize(600, 400);
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation(dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2);
	    setResizable(false);
	    setLayout(null);

	    // Background Panel
	    backgroundPanel = new LImagePane(LImages.multipurpose());
	    super.add(backgroundPanel);
	    skyprocLogo = new LImagePane(SPDefaultGUI.class.getResource("SkyProcLogoSmall.png"));
	    skyprocLogo.setLocation(getWidth() - skyprocLogo.getWidth() - 15, this.getHeight() - skyprocLogo.getHeight() - 30);
	    backgroundPanel.add(skyprocLogo);


	    // Label
	    pluginLabel = new LLabel("[ " + pluginName + " ]", LFonts.OptimusPrinceps(30), new Color(61, 143, 184));
	    pluginLabel.addShadow();
	    pluginLabel.centerIn(this, 20);
	    descriptionAnchor = pluginLabel;
	    backgroundPanel.add(pluginLabel);


	    //Creating Patch
	    patching = new LLabel("Creating patch.", LFonts.Typo3(15), new Color(210, 210, 210));
	    patching.addShadow();
	    patching.centerIn(this, this.getHeight() - patching.getHeight() - 80);
	    backgroundPanel.add(patching);

	    //ProgressBar
	    pbar.centerIn(this, patching.getY() + patching.getHeight() + 5);
	    pbar.setCentered(false);
	    pbar.setStatusOffset(-5);
	    LLabel status = new LLabel(". . .", new Font("SansSerif", Font.PLAIN, 11), new Color(160, 160, 160));
	    pbar.setStatusLabel(status);
	    status.setLocation(8, getHeight() - status.getHeight() - 36);
	    backgroundPanel.add(status);
	    backgroundPanel.add(pbar);

	    //Description
	    int descY = descriptionAnchor.getY() + descriptionAnchor.getHeight() + 20;
	    Dimension descSize = new Dimension(this.getWidth() - 100, this.getHeight() - descY
		    - (this.getHeight() - patching.getY()) - 15);
	    description = new LTextPane(descSize, new Color(200, 200, 200));
	    description.centerIn(this, descY);
	    description.setEditable(false);
	    description.setText(descriptionText);
	    description.setFontSize(14);
	    description.centerText();
	    backgroundPanel.add(description);


	    setVisible(true);
	} catch (IOException ex) {
	}
    }

    void finishRun() {
	patching.setText("Patch is complete!");
	patching.centerIn(this, patching.getY());
	patching.setFontColor(Color.orange);
    }

    /**
     *
     * @param c GUI component to add to the default GUI
     * @return The component added.
     */
    @Override
    public Component add(final Component c) {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		backgroundPanel.add(c, 0);
	    }
	});
	return c;
    }

    /**
     * This function will replace the default text-based header with an image
     * you supply.
     *
     * @param logo URL to an image.
     * @param descriptionOffset Y-offset to give the description box, to help
     * align where it should begin in relation to your image.
     * @throws IOException If the image given by the URL cannot be loaded
     * properly.
     */
    public void replaceHeader(final URL logo, final int descriptionOffset) throws IOException {
	patcherLogo = new LImagePane(logo);
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		description.setVisible(false);
		patcherLogo.setLocation(getWidth() / 2 - patcherLogo.getWidth() / 2,
			0);
		pluginLabel.setVisible(false);
		backgroundPanel.add(patcherLogo, 0);
		fitDesc(patcherLogo, descriptionOffset);
	    }
	});
    }

    void fitDesc(final Component descAnchor, final int offset) {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		description.setVisible(false);
		int descY = descAnchor.getY() + descAnchor.getHeight() + 20;
		Dimension descSize = new Dimension(getWidth() - 100, getHeight()
			- descY - (getHeight() - patching.getY()) - 15 - offset);
		description.setLocation(description.getX(), descY + offset);
		description.setSize(descSize);
		description.setVisible(true);
	    }
	});
    }

    /**
     * Sets the title text color in the default GUI.
     *
     * @param c Color to set the title to.
     */
    public void setHeaderColor(final Color c) {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		pluginLabel.setFontColor(c);
	    }
	});
    }

    /**
     * Tells the default GUI to switch the text and tell the user the patch is
     * complete.
     */
    public void finished() {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		finishRun();
	    }
	});
    }
}
