# SettingsSearch

This is a library for Android apps that allows to search inside Preferences.

<img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/main.png" />
<img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/history.png" />
<img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/suggestions.png" />
<img width="200" src="https://raw.githubusercontent.com/ByteHamster/PreferenceSearch/master/screenshots/result.png" />

## Adding to your app

Add PreferenceSearch to your `app/build.gradle`:

    dependencies {
        implementation 'com.github.KnollFrank:SettingsSearch:-SNAPSHOT'
    }

Add PreferenceSearch to your `build.gradle`:

    allprojects {
        repositories {
            // ...
            maven { url 'https://jitpack.io' }
        }
    }

Add search bar to your `preferences.xml` file:

    <com.bytehamster.lib.preferencesearch.SearchPreference
        android:key="searchPreference" />
        
Define search index in your `PreferenceFragment`:


    public static class PrefsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            SearchPreference searchPreference = (SearchPreference) findPreference("searchPreference");
            SearchConfiguration config = searchPreference.getSearchConfiguration();
            config.setActivity((AppCompatActivity) getActivity());
            config.index(R.xml.preferences);
        }
    }

And react to search results in your Activity:

    public class MyActivity extends AppCompatActivity implements SearchPreferenceResultListener {
        private PrefsFragment prefsFragment;

        @Override
        public void onSearchResultClicked(SearchPreferenceResult result) {
            result.closeSearchPage(this);
            result.highlight(prefsFragment);
        }
    }

## Translations

This library currently contains only a limited number of translations. If you want to translate
the texts shown by the library together with your app's other strings, you can just override
the strings defined in `lib/src/main/res/values/strings.xml` in your own application by copying
those lines to your app's `strings.xml`.
