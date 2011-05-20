package com.limegroup.gnutella.gui.search;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;

import org.jdesktop.swingx.JXCollapsiblePane;

import com.limegroup.gnutella.MediaType;
import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.IconManager;
import com.limegroup.gnutella.gui.KeyProcessingTextField;
import com.limegroup.gnutella.gui.PaddedPanel;
import com.limegroup.gnutella.gui.actions.FileMenuActions;
import com.limegroup.gnutella.gui.actions.FileMenuActions.OpenMagnetTorrentAction;
import com.limegroup.gnutella.gui.themes.SkinHandler;
import com.limegroup.gnutella.gui.themes.ThemeSettings;
import com.limegroup.gnutella.gui.xml.InputPanel;
import com.limegroup.gnutella.settings.SearchSettings;

/**
 * Inner panel that switches between the various kinds of
 * searching.
 */
class SearchInputPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -5638062215253666235L;

    /**
     * The current search label in normal search.
     */
    private final JLabel SEARCH_TYPE_LABEL = new JLabel();

    /**
     * The sole input text field that is at the top of all searches.
     */
    private final SearchField SEARCH_FIELD = new SearchField(14);

    /**
     * The JTabbedPane that switches between types of searches.
     */
    private final JTabbedPane PANE = new JTabbedPane(JTabbedPane.BOTTOM);

    /**
     * The CardLayout that switches between the detailed
     * search input information for each meta-type.
     */
    private final CardLayout META_CARDS = new CardLayout();

    /**
     * The panel that the META_CARDS layout uses to layout
     * the detailed search input fields.
     */
    private final JPanel META_PANEL = new JPanel(META_CARDS);

    /**
     * The name to use for the default panel that has no meta-data.
     */
    private static final String DEFAULT_PANEL_KEY = "defaultPanel";

    /**
     * The box that holds the schemas for searching.
     */

    /**
     * The ditherer to use for the tab backgrounds.
     */
    private final Ditherer DITHERER = new Ditherer(62, SkinHandler.getSearchPanelBG1(), SkinHandler.getSearchPanelBG2());

    private JPanel searchEntry;

    /**
     * The listener for new searches.
     */
    private final ActionListener SEARCH_LISTENER = new SearchListener();

	private JXCollapsiblePane SEARCH_OPTIONS_COLLAPSIBLE_PANEL;

    SearchInputPanel() {
        super(new BorderLayout(0, 5));

        final ActionListener schemaListener = new SchemaListener();

        searchEntry = createSearchEntryPanel();
        panelize(searchEntry);

        PANE.add(I18n.tr("Search"), searchEntry);
        PANE.setRequestFocusEnabled(false);
        PANE.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestSearchFocusImmediately();
            }
        });

        add(PANE, BorderLayout.CENTER);

        Font bold = UIManager.getFont("Table.font.bold");
        Font bolder = new Font(bold.getName(), bold.getStyle(), bold.getSize() + 5);
        SEARCH_TYPE_LABEL.setFont(bolder);
        SEARCH_TYPE_LABEL.setPreferredSize(new Dimension(130, 20));
        schemaListener.actionPerformed(null);
    }

    /**
     * Gets the KeyProcessingTextField that key events can be forwarded to.
     */
    KeyProcessingTextField getForwardingSearchField() {
        if (isNormalSearchType()) {
            return SEARCH_FIELD;
        }
        return null;
    }

    /**
     * Determines if a key event can be forwarded to the search.
     */
    boolean isKeyEventForwardable() {
        return isNormalSearchType();
    }

    /**
     * Determines if keyword is selected.
     */
    boolean isNormalSearchType() {
        return PANE.getSelectedIndex() == 0;
    }

    void requestSearchFocusImmediately() {
        if (getInputPanel() != null) {
            getInputPanel().requestFirstFocus();
        } else if (SEARCH_FIELD != null) {
            SEARCH_FIELD.requestFocus();
        }
    }

    void requestSearchFocus() {
        // Workaround for bug manifested on Java 1.3 where FocusEvents
        // are improperly posted, causing BasicTabbedPaneUI to throw an
        // ArrayIndexOutOfBoundsException.
        // See:
        // http://developer.java.sun.com/developer/bugParade/bugs/4523606.html
        // http://developer.java.sun.com/developer/bugParade/bugs/4379600.html
        // http://developer.java.sun.com/developer/bugParade/bugs/4128120.html
        // for related problems.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                requestSearchFocusImmediately();
            }
        });
    }

    /**
     * Sets all components in this component to be not opaque
     * and sets the correct background panel.
     */
    private void panelize(JComponent c) {
        GUIUtils.setOpaque(false, c);
        if (!ThemeSettings.isNativeTheme()) {
            c.setOpaque(true);
        }

        c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
    }

    private JPanel createSearchEntryPanel() {
        SEARCH_FIELD.addActionListener(SEARCH_LISTENER);

        // add the default search input panel to the meta cards
        META_PANEL.add(createDefaultSearchPanel(), DEFAULT_PANEL_KEY);

        // other mediatype panels are added lazily on demand

        JPanel search = new DitherPanel(DITHERER);
        search.setLayout(new BoxLayout(search, BoxLayout.Y_AXIS));
        search.add(GUIUtils.left(SEARCH_TYPE_LABEL));
        search.add(Box.createVerticalStrut(5));
        search.add(META_PANEL);

        JButton openTorrentButton = new JButton("Open a Torrent");
        openTorrentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OpenMagnetTorrentAction openMagnetTorrentAction = new FileMenuActions.OpenMagnetTorrentAction();
                openMagnetTorrentAction.actionPerformed(null);
            }
        });

        search.add(GUIUtils.center(openTorrentButton));
        return search;
    }

    /**
     * Creates the default search input of:
     *    Filename
     *    [   input box  ]
     */
    private JPanel createDefaultSearchPanel() {
        JPanel fullPanel = new BoxPanel(BoxPanel.Y_AXIS);
        fullPanel.add(Box.createVerticalStrut(3));
        fullPanel.add(GUIUtils.left(SEARCH_FIELD));
        fullPanel.add(Box.createVerticalStrut(5));
        fullPanel.add(createSearchButtonPanel());
        fullPanel.add(createSearchOptionsPanel());
        return GUIUtils.left(fullPanel);
    }

    private Component createSearchOptionsPanel() {
		SEARCH_OPTIONS_COLLAPSIBLE_PANEL = new JXCollapsiblePane();
		SEARCH_OPTIONS_COLLAPSIBLE_PANEL.setCollapsed(true);
		SEARCH_OPTIONS_COLLAPSIBLE_PANEL.setLayout(new GridLayout(0, 1));
		SEARCH_OPTIONS_COLLAPSIBLE_PANEL.setAnimated(true);
		
		
		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS));
		final JCheckBox checkboxClearbits = new JCheckBox("Clearbits");
		final JCheckBox checkBoxMininova = new JCheckBox("Mininova");
		final JCheckBox checkBoxISOHunt = new JCheckBox("IsoHunt");
		
		checkboxClearbits.setSelected(true);
        checkBoxMininova.setSelected(true);
        checkBoxISOHunt.setSelected(true);
		
		ItemListener listener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (!checkboxClearbits.isSelected() &&
                    !checkBoxMininova.isSelected() &&
                    !checkBoxISOHunt.isSelected()) {
                    ((JCheckBox)e.getItemSelectable()).setSelected(true);
                }
            }
        };
		
		checkboxClearbits.addItemListener(listener);
		checkBoxMininova.addItemListener(listener);
		checkBoxISOHunt.addItemListener(listener);
		
		controls.add(checkboxClearbits);
		controls.add(checkBoxMininova);
		controls.add(checkBoxISOHunt);
		
		controls.setBorder(new TitledBorder("Choose Search Engines"));
		
		SEARCH_OPTIONS_COLLAPSIBLE_PANEL.add(controls, BorderLayout.PAGE_START);

		return SEARCH_OPTIONS_COLLAPSIBLE_PANEL;
	}

	/**
     * Creates the search button & inserts it in a panel.
     */
    private JPanel createSearchButtonPanel() {
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton searchButton = new JButton(I18n.tr("Search"));
        searchButton.setToolTipText(I18n.tr("Search the Network for the Given Words"));

        searchButton.addActionListener(SEARCH_LISTENER);

        b.add(Box.createHorizontalGlue());
        b.add(searchButton);
        

        // add collapse button
        PaddedPanel paddedButtonPanel = new PaddedPanel();
        
        JButton iconButton = new JButton();
        iconButton.setAction(new ToggleSearchOptionsPanelAction());
        iconButton.setIcon(IconManager.instance().getSmallIconForButton("SEARCH_OPTIONS_MORE"));
        fixIconButton(iconButton);        
        
        paddedButtonPanel.add(iconButton);
        
        b.add(paddedButtonPanel);

        return b;
    }

	private void fixIconButton(JButton iconButton) {
		iconButton.setBorderPainted(false);
        iconButton.setFocusable(false);
        iconButton.setBorder(null);
        iconButton.setFocusPainted(false);
        iconButton.setContentAreaFilled(false);
	}

    /**
     * Gets the visible component in META_PANEL.
     */
    private JComponent getVisibleComponent() {
        for (int i = 0; i < META_PANEL.getComponentCount(); i++) {
            Component current = META_PANEL.getComponent(i);
            if (current.isVisible())
                return (JComponent) current;
        }
        return null;
    }

    /**
     * Gets the visible scrollpane.
     */
    private JScrollPane getVisibleScrollPane() {
        JComponent parent = (JComponent) getVisibleComponent().getComponent(0);
        for (int i = 0; i < parent.getComponentCount(); i++) {
            Component current = parent.getComponent(i);
            if (current.isVisible() && current instanceof JScrollPane)
                return (JScrollPane) current;
        }
        return null;
    }

    /**
     * Retrieves the InputPanel that is currently visible.
     */
    private InputPanel getInputPanel() {
        JScrollPane pane = getVisibleScrollPane();
        if (pane == null)
            return null;
        else
            return (InputPanel) pane.getViewport().getView();
    }

    /**
     * Listener for selecting a new schema.
     */
    private class SchemaListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            SearchSettings.MAX_QUERY_LENGTH.revertToDefault();

            //Truncate if you have too much text for a gnutella search
            if (SEARCH_FIELD.getText().length() > SearchSettings.MAX_QUERY_LENGTH.getValue()) {
                try {
                    SEARCH_FIELD.setText(SEARCH_FIELD.getText(0, SearchSettings.MAX_QUERY_LENGTH.getValue()));
                } catch (BadLocationException e) {
                }
            }

            SEARCH_TYPE_LABEL.setText(I18n.tr("Search Files"));
            requestSearchFocus();
        }
    }

    /**
     * Listener for starting a new search.
     */
    private class SearchListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            InputPanel panel = getInputPanel();
            String query = SEARCH_FIELD.getText();
            final SearchInformation info = SearchInformation.createTitledKeywordSearch(query, null, MediaType.TYPE_TORRENTS, query);

            // If the search worked, store & clear it.
            if (SearchMediator.triggerSearch(info) != null) {
                if (info.isKeywordSearch()) {
                    // Add the necessary stuff for autocompletion.
                    if (panel != null) {
                        panel.storeInput();
                        panel.clear();
                    } else {
                        SEARCH_FIELD.addToDictionary();
                    }

                    // Clear the existing search.
                    SEARCH_FIELD.setText("");
                }
            }
        }
    }
    
    private class ToggleSearchOptionsPanelAction extends AbstractAction {

		/**
         * 
         */
        private static final long serialVersionUID = -2415729526575357348L;

        @Override
		public void actionPerformed(ActionEvent event) {
			SEARCH_OPTIONS_COLLAPSIBLE_PANEL.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION).actionPerformed(event);
			
			JButton iconButton = (JButton) event.getSource();
			
			Icon iconForButton = null;
			
			if (!SEARCH_OPTIONS_COLLAPSIBLE_PANEL.isCollapsed()) {
				iconForButton = IconManager.instance().getSmallIconForButton("SEARCH_OPTIONS_LESS");
			} else {
				iconForButton = IconManager.instance().getSmallIconForButton("SEARCH_OPTIONS_MORE");	
			}

			iconButton.setIcon(iconForButton);
			fixIconButton(iconButton);
		}
    	
    }
}
