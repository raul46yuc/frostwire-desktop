/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(TM). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.frostwire.gui.library;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.TableUI;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;
import org.pushingpixels.substance.internal.ui.SubstanceTableUI;
import org.pushingpixels.substance.internal.ui.SubstanceTableUI.TableCellId;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.UpdateOptimizationInfo;

import com.limegroup.gnutella.gui.themes.ThemeSettings;

/**
 *  Creates both a renderer and an editor for cells in the playlist table that display the name
 *  of the file being played.
 */
class PlaylistItemPropertyRenderer extends SubstanceDefaultTableCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 5077708462742409205L;

    public PlaylistItemPropertyRenderer() {
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        PlaylistItemProperty property = (PlaylistItemProperty) value;
        super.getTableCellRendererComponent(table, property.getValue(), isSelected, hasFocus, row, column);
        setFontColor(property.isPlaying(), property.exists(), table, row, column);
        return this;
    }

    /**
     * Check what font color to use if this song is playing. 
     */
    private void setFontColor(boolean playing, boolean exists, JTable table, int row, int column) {
        if (!exists) {
            setForeground(ThemeSettings.FILE_NO_EXISTS_DATA_LINE_COLOR.getValue());
        } else if (playing) {
            setForeground(ThemeSettings.PLAYING_DATA_LINE_COLOR.getValue());
        } else {
            Color color = Color.BLACK;
            if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
                color = getSubstanceForegroundColor(table, row, column);
            } else {
                color = UIManager.getColor("Table.foreground");
            }

            setForeground(color);
        }
    }

    private Color getSubstanceForegroundColor(JTable table, int row, int column) {
        TableUI tableUI = table.getUI();
        SubstanceTableUI ui = (SubstanceTableUI) tableUI;
        TableCellId cellId = new TableCellId(row, column);
        ComponentState currState = ui.getCellState(cellId);

        SubstanceColorScheme scheme = getColorSchemeForState(table, ui, currState);

        return scheme.getForegroundColor();
    }

    private SubstanceColorScheme getColorSchemeForState(JTable table, SubstanceTableUI ui, ComponentState state) {
        UpdateOptimizationInfo updateOptimizationInfo = ui.getUpdateOptimizationInfo();
        if (state == ComponentState.ENABLED) {
            if (updateOptimizationInfo == null) {
                return SubstanceColorSchemeUtilities.getColorScheme(table, state);
            } else {
                return updateOptimizationInfo.getDefaultScheme();
            }
        } else {
            if (updateOptimizationInfo == null) {
                return SubstanceColorSchemeUtilities.getColorScheme(table, ColorSchemeAssociationKind.HIGHLIGHT, state);
            } else {
                return updateOptimizationInfo.getHighlightColorScheme(state);
            }
        }
    }
}