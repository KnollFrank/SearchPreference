package de.KnollFrank.lib.settingssearch;

import androidx.preference.Preference;

import org.jgrapht.graph.DefaultEdge;

public class PreferenceEdge extends DefaultEdge {

    public final Preference preference;

    public PreferenceEdge(final Preference preference) {
        this.preference = preference;
    }

    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + " : " + preference + ")";
    }
}
