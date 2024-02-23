package br.com.pdasolucoes.standardconfig.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigurationHelper {

    private ConfigurationHelper() {

    }

    public static void savePreference(ConfigurationEntry entry, Boolean value) {
        SharedPreferences preferences = MyApplication.getInstance().getSharedPreferences(entry.getCatalogName(), Context.MODE_PRIVATE);
        Editor preferencesEditor = preferences.edit();
        preferencesEditor.putBoolean(entry.getKeyName(), value);
        preferencesEditor.apply();
    }

    public static Boolean loadPreference(ConfigurationEntry entry, Boolean defaultValue) {
        SharedPreferences preferences = MyApplication.getInstance().getSharedPreferences(entry.getCatalogName(), Context.MODE_PRIVATE);
        return preferences.getBoolean(entry.getKeyName(), defaultValue);
    }

    public static void savePreference(ConfigurationEntry entry, String value) {
        String encryptedValue = Cryptography.encrypt(value);

        SharedPreferences preferences = MyApplication.getInstance().getSharedPreferences(entry.getCatalogName(), Context.MODE_PRIVATE);
        Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(entry.getKeyName(), encryptedValue);
        preferencesEditor.apply();
    }

    public static void savePreference(ConfigurationEntry entry, int value) {
        SharedPreferences preferences = MyApplication.getInstance().getSharedPreferences(entry.getCatalogName(), Context.MODE_PRIVATE);
        Editor preferencesEditor = preferences.edit();
        preferencesEditor.putInt(entry.getKeyName(), value);
        preferencesEditor.apply();
    }

    public static String loadPreference(ConfigurationEntry entry, String defaultValue) {
        SharedPreferences preferences = MyApplication.getInstance().getSharedPreferences(entry.getCatalogName(), Context.MODE_PRIVATE);
        String encryptedValue = preferences.getString(entry.getKeyName(), defaultValue);
        return Cryptography.decrypt(encryptedValue);
    }

    public static int loadPreference(ConfigurationEntry entry, int defaultValue) {
        SharedPreferences preferences = MyApplication.getInstance().getSharedPreferences(entry.getCatalogName(), Context.MODE_PRIVATE);
        return preferences.getInt(entry.getKeyName(), defaultValue);
    }

    public enum Catalog {
        Configuration("CONFIGURATION"),
        Authentication("AUTHENTICATION"),
        Application("APPLICATION"),
        Printer("PRINTER"),
        Credentials("CREDENTIALS");

        private String name;

        private Catalog(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum ConfigurationEntry {
        ServerAddress(Catalog.Credentials, "SERVERADDRESS"),
        ServerAddressApi(Catalog.Credentials, "SERVERADDRESSAPI"),
        Directory(Catalog.Credentials, "DIRECTORY"),
        Store(Catalog.Credentials, "STORE"),
        UserName(Catalog.Authentication, "USERNAME"),
        UserCode(Catalog.Authentication, "USERCODE"),
        UserCodeFilial(Catalog.Authentication, "USERCODEFILIAL"),
        UserNameFilial(Catalog.Authentication, "USERNAMEFILIAL"),
        UserCodeProfile(Catalog.Authentication, "USERCODEPROFILE"),
        UserNameProfile(Catalog.Authentication, "USERNAMEPROFILE"),
        UserLeader(Catalog.Authentication, "USERLEADER"),
        UserLogin(Catalog.Authentication, "USERLOGIN"),
        UserPassword(Catalog.Authentication, "USERPASSWORD"),
        IsLoggedIn(Catalog.Authentication, "ISLOGGEDIN"),
        RefreshToken(Catalog.Authentication, "REFRESHTOKEN"),
        Token(Catalog.Authentication, "TOKEN"),
        TimeOutSession(Catalog.Authentication, "TIMEOUTSESSION"),
        PackageName(Catalog.Application, "PACKAGENAME"),
        Modulo(Catalog.Application, "MODULO"),
        MacAddress(Catalog.Printer, "MACADDRESS"),
        IsConfigured(Catalog.Credentials, "ISCONFIGURED");

        private Catalog catalog;
        private String name;

        ConfigurationEntry(Catalog catalog, String name) {
            this.catalog = catalog;
            this.name = name;
        }

        public String getCatalogName() {
            return this.catalog.getName();
        }

        public String getKeyName() {
            return this.name;
        }
    }

    public enum TypeMessage {
        Updated(0),
        Changed(1),
        New(2),
        Removed(3),
        None(4);

        private int intValue;

        private TypeMessage(int value) {
            intValue = value;
        }

        public int getValue() {
            return intValue;
        }
    }

}
