package de.KnollFrank.lib.preferencesearch;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.KnollFrank.lib.preferencesearch.common.Maps;
import de.KnollFrank.lib.preferencesearch.common.Preferences;

class PreferenceScreenGraphProvider {

    private final PreferenceScreenWithHostProvider preferenceScreenWithHostProvider;

    public PreferenceScreenGraphProvider(final PreferenceScreenWithHostProvider preferenceScreenWithHostProvider) {
        this.preferenceScreenWithHostProvider = preferenceScreenWithHostProvider;
    }

    public Graph<PreferenceScreenWithHost, PreferenceEdge> getPreferenceScreenGraph(final PreferenceScreenWithHost root) {
        final Graph<PreferenceScreenWithHost, PreferenceEdge> preferenceScreenGraph = new DefaultDirectedGraph<>(PreferenceEdge.class);
        buildPreferenceScreenGraph(root, preferenceScreenGraph);
        return preferenceScreenGraph;
    }

    private void buildPreferenceScreenGraph(final PreferenceScreenWithHost root,
                                            final Graph<PreferenceScreenWithHost, PreferenceEdge> preferenceScreenGraph) {
        if (preferenceScreenGraph.containsVertex(root)) {
            return;
        }
        preferenceScreenGraph.addVertex(root);
        this
                .getConnectedPreferenceScreenByPreference(root.preferenceScreen)
                .forEach(
                        (preference, child) -> {
                            buildPreferenceScreenGraph(child, preferenceScreenGraph);
                            preferenceScreenGraph.addVertex(child);
                            preferenceScreenGraph.addEdge(root, child, new PreferenceEdge(preference));
                        });
    }

    private Map<Preference, PreferenceScreenWithHost> getConnectedPreferenceScreenByPreference(final PreferenceScreen preferenceScreen) {
        return Maps.filterPresentValues(
                Preferences
                        .getAllChildren(preferenceScreen)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Function.identity(),
                                        this::getConnectedPreferenceScreen)));
    }

    private Optional<PreferenceScreenWithHost> getConnectedPreferenceScreen(final Preference preference) {
        final String fragment = preference.getFragment();
        return fragment != null ?
                this.preferenceScreenWithHostProvider.getPreferenceScreenOfFragment(fragment) :
                Optional.empty();
    }
}
