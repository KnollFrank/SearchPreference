package de.KnollFrank.lib.settingssearch.db.preference.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Optional;

import de.KnollFrank.lib.settingssearch.db.preference.SearchablePreference;
import de.KnollFrank.lib.settingssearch.db.preference.dao.POJOTestFactory;
import de.KnollFrank.lib.settingssearch.db.preference.pojo.SearchablePreferencePOJO;
import de.KnollFrank.settingssearch.test.TestActivity;

@RunWith(RobolectricTestRunner.class)
public class SearchablePreferenceFromPOJOConverterTest {

    @Test
    public void shouldConvertSearchablePreferencePOJO2SearchablePreference() {
        try (final ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(activity -> {
                // Given
                final SearchablePreferencePOJO pojo = POJOTestFactory.createSomeSearchablePreferencePOJO();

                // When
                final SearchablePreference searchablePreference = SearchablePreferenceFromPOJOConverter.convertFromPOJO(pojo, activity);

                // Then
                assertEquals(searchablePreference, pojo);
            });
        }
    }

    private static void assertEquals(final SearchablePreference actual, final SearchablePreferencePOJO expected) {
        assertThat(actual.getKey(), is(expected.key()));
        // FK-TODO: handle correctly: assertThat(actual.getIcon(), is(expected.iconResId()));
        assertThat(actual.getLayoutResource(), is(expected.layoutResId()));
        assertThat(actual.getSummary(), is(expected.summary()));
        assertThat(actual.getTitle(), is(expected.title()));
        assertThat(actual.getWidgetLayoutResource(), is(expected.widgetLayoutResId()));
        assertThat(actual.getFragment(), is(expected.fragment()));
        assertThat(actual.isVisible(), is(expected.visible()));
        assertThat(actual.getSearchableInfo(), is(Optional.ofNullable(expected.searchableInfo())));
    }
}