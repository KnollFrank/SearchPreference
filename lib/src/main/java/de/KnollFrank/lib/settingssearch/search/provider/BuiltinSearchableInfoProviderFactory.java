package de.KnollFrank.lib.settingssearch.search.provider;

import androidx.preference.DropDownPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

import de.KnollFrank.lib.settingssearch.common.Lists;

public class BuiltinSearchableInfoProviderFactory {

    public static SearchableInfoProvider getBuiltinSearchableInfoProvider() {
        return preference -> {
            if (hasClass(preference, ListPreference.class)) {
                return Optional.of(getListPreferenceSearchableInfo((ListPreference) preference));
            }
            if (hasClass(preference, SwitchPreference.class)) {
                return Optional.of(getSwitchPreferenceSearchableInfo((SwitchPreference) preference));
            }
            if (hasClass(preference, DropDownPreference.class)) {
                return Optional.of(getDropDownPreferenceSearchableInfo((DropDownPreference) preference));
            }
            if (hasClass(preference, MultiSelectListPreference.class)) {
                return Optional.of(getMultiSelectListPreferenceSearchableInfo((MultiSelectListPreference) preference));
            }
            return Optional.empty();
        };
    }

    private static boolean hasClass(final Preference preference, final Class<? extends Preference> preferenceClass) {
        return preference.getClass().equals(preferenceClass);
    }

    private static String getListPreferenceSearchableInfo(final ListPreference listPreference) {
        return String.join(
                ", ",
                concat(
                        Optional.ofNullable(listPreference.getDialogTitle()),
                        Optional.ofNullable(listPreference.getEntries())));
    }

    private static String getSwitchPreferenceSearchableInfo(final SwitchPreference switchPreference) {
        return String.join(
                ", ",
                Lists.getPresentElements(
                        ImmutableList.of(
                                Optional.ofNullable(switchPreference.getSummaryOff()),
                                Optional.ofNullable(switchPreference.getSummaryOn()))));
    }

    private static String getDropDownPreferenceSearchableInfo(final DropDownPreference dropDownPreference) {
        return String.join(
                ", ",
                Lists.asList(Optional.ofNullable(dropDownPreference.getEntries())));
    }

    private static String getMultiSelectListPreferenceSearchableInfo(final MultiSelectListPreference multiSelectListPreference) {
        return String.join(
                ", ",
                concat(
                        Optional.ofNullable(multiSelectListPreference.getDialogTitle()),
                        Optional.ofNullable(multiSelectListPreference.getEntries())));
    }

    private static List<CharSequence> concat(final Optional<CharSequence> dialogTitle,
                                             final Optional<CharSequence[]> entries) {
        return ImmutableList
                .<CharSequence>builder()
                .addAll(Lists.getPresentElements(List.of(dialogTitle)))
                .addAll(Lists.asList(entries))
                .build();
    }
}
