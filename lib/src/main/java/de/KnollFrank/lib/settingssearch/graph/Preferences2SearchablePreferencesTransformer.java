package de.KnollFrank.lib.settingssearch.graph;

import androidx.preference.Preference;

import org.jgrapht.Graph;

import de.KnollFrank.lib.settingssearch.PreferenceEdge;
import de.KnollFrank.lib.settingssearch.PreferenceScreenWithHost;
import de.KnollFrank.lib.settingssearch.common.GraphTransformer;
import de.KnollFrank.lib.settingssearch.common.IGraphTransformer;
import de.KnollFrank.lib.settingssearch.db.SearchableInfoAndDialogInfoProvider;
import de.KnollFrank.lib.settingssearch.db.SearchablePreferenceTransformer;
import de.KnollFrank.lib.settingssearch.db.preference.SearchablePreference;
import de.KnollFrank.lib.settingssearch.provider.IsPreferenceSearchable;

class Preferences2SearchablePreferencesTransformer {

    private final IsPreferenceSearchable isPreferenceSearchable;
    private final SearchableInfoAndDialogInfoProvider searchableInfoAndDialogInfoProvider;

    public Preferences2SearchablePreferencesTransformer(final IsPreferenceSearchable isPreferenceSearchable,
                                                        final SearchableInfoAndDialogInfoProvider searchableInfoAndDialogInfoProvider) {
        this.isPreferenceSearchable = isPreferenceSearchable;
        this.searchableInfoAndDialogInfoProvider = searchableInfoAndDialogInfoProvider;
    }

    public Graph<SearchablePreferenceScreenWithMapAndHost, PreferenceEdge> transformPreferences2SearchablePreferences(
            final Graph<PreferenceScreenWithHost, PreferenceEdge> preferenceScreenGraph) {
        return GraphTransformer.transform(
                preferenceScreenGraph,
                PreferenceEdge.class,
                createGraphTransformer());
    }

    private IGraphTransformer<PreferenceScreenWithHost, PreferenceEdge, SearchablePreferenceScreenWithMapAndHost, PreferenceEdge> createGraphTransformer() {
        return new IGraphTransformer<>() {

            @Override
            public SearchablePreferenceScreenWithMapAndHost transformNode(final PreferenceScreenWithHost preferenceScreenWithHost) {
                final SearchablePreferenceTransformer transformer =
                        new SearchablePreferenceTransformer(
                                preferenceScreenWithHost.host().getPreferenceManager(),
                                preferenceScreenWithHost.host(),
                                isPreferenceSearchable,
                                searchableInfoAndDialogInfoProvider);
                return new SearchablePreferenceScreenWithMapAndHost(
                        transformer.transform2SearchablePreferenceScreen(preferenceScreenWithHost.host().getPreferenceScreen()),
                        preferenceScreenWithHost.host());
            }

            @Override
            public PreferenceEdge transformEdge(final PreferenceEdge edge, final SearchablePreferenceScreenWithMapAndHost transformedParentNode) {
                return new PreferenceEdge(getSearchablePreference(edge.preference, transformedParentNode));
            }

            private SearchablePreference getSearchablePreference(final Preference preference,
                                                                 final SearchablePreferenceScreenWithMapAndHost transformedParentNode) {
                return transformedParentNode
                        .searchablePreferenceScreenWithMap()
                        .searchablePreferenceByPreference()
                        .get(preference);
            }
        };
    }
}