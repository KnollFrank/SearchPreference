package com.bytehamster.lib.preferencesearch;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PreferencesGraphProvider {

    private final FragmentActivity fragmentActivity;

    public PreferencesGraphProvider(final FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public Graph<PreferenceScreen, DefaultEdge> getPreferencesGraph(final PreferenceFragmentCompat root) {
        final Graph<PreferenceScreen, DefaultEdge> preferencesGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
        initialize(root);
        buildPreferencesGraph(preferencesGraph, root.getPreferenceScreen());
        return preferencesGraph;
    }

    private void buildPreferencesGraph(final Graph<PreferenceScreen, DefaultEdge> preferencesGraph,
                                       final PreferenceScreen root) {
        preferencesGraph.addVertex(root);
        for (final PreferenceScreen child : getChildren(root)) {
            preferencesGraph.addVertex(child);
            preferencesGraph.addEdge(root, child);
            buildPreferencesGraph(preferencesGraph, child);
        }
    }

    private void initialize(final Fragment fragment) {
        this
                .fragmentActivity
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .commitNow();
    }

    private List<PreferenceScreen> getChildren(final PreferenceScreen preferenceScreen) {
        return PreferenceParser
                .getPreferences(preferenceScreen)
                .stream()
                .map(Preference::getFragment)
                .filter(Objects::nonNull)
                .map(this::getPreferenceScreenOfFragment)
                .collect(Collectors.toList());
    }

    private PreferenceScreen getPreferenceScreenOfFragment(final String fragment) {
        final PreferenceFragmentCompat preferenceFragmentCompat =
                (PreferenceFragmentCompat) Fragment.instantiate(
                        this.fragmentActivity,
                        fragment,
                        null);
        initialize(preferenceFragmentCompat);
        return preferenceFragmentCompat.getPreferenceScreen();
    }
}
