/** filtered and transformed by ARG-V */

/*
 *
 * Autopsy Forensic Browser
 *
 * Copyright 2012-2021 Basis Technology Corp.
 *
 * Copyright 2012 42six Solutions.
 *
 * Project Contact/Architect: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.sosy_lab.sv_benchmarks.Verifier;
import java.io.BufferedReader;
import java.util.logging.Level;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * Chromium recent activity extraction
 */
class Main {

    private static String HISTORY_QUERY = "SELECT urls.url, urls.title, urls.visit_count, urls.typed_count, " //NON-NLS
            + "last_visit_time, urls.hidden, visits.visit_time, (SELECT urls.url FROM urls WHERE urls.id=visits.url) AS from_visit, visits.transition FROM urls, visits WHERE urls.id = visits.url"; //NON-NLS
    private static String COOKIE_QUERY = "SELECT name, value, host_key, expires_utc,last_access_utc, creation_utc FROM cookies"; //NON-NLS
    private static String DOWNLOAD_QUERY = "SELECT full_path, url, start_time, received_bytes FROM downloads"; //NON-NLS
    private static String DOWNLOAD_QUERY_V30 = "SELECT current_path AS full_path, url, start_time, received_bytes FROM downloads, downloads_url_chains WHERE downloads.id=downloads_url_chains.id"; //NON-NLS
    private static String LOGIN_QUERY = "SELECT origin_url, username_value, date_created, signon_realm from logins"; //NON-NLS
    private static String AUTOFILL_QUERY = "SELECT name, value, count, date_created "
            + " FROM autofill, autofill_dates "
            + " WHERE autofill.pair_id = autofill_dates.pair_id"; //NON-NLS
    private static String AUTOFILL_QUERY_V8X = "SELECT name, value, count, date_created, date_last_used from autofill"; //NON-NLS
    private static String WEBFORM_ADDRESS_QUERY = "SELECT first_name, middle_name, last_name, address_line_1, address_line_2, city, state, zipcode, country_code, number, email, date_modified "
            + " FROM autofill_profiles, autofill_profile_names, autofill_profile_emails, autofill_profile_phones"
            + " WHERE autofill_profiles.guid = autofill_profile_names.guid AND autofill_profiles.guid = autofill_profile_emails.guid AND autofill_profiles.guid = autofill_profile_phones.guid";

    private static String WEBFORM_ADDRESS_QUERY_V8X = "SELECT first_name, middle_name, last_name, full_name, street_address, city, state, zipcode, country_code, number, email, date_modified, use_date, use_count"
            + " FROM autofill_profiles, autofill_profile_names, autofill_profile_emails, autofill_profile_phones"
            + " WHERE autofill_profiles.guid = autofill_profile_names.guid AND autofill_profiles.guid = autofill_profile_emails.guid AND autofill_profiles.guid = autofill_profile_phones.guid";
    private static String FAVICON_QUERY = "SELECT page_url, last_updated, last_requested FROM icon_mapping, favicon_bitmaps "
            + " WHERE icon_mapping.icon_id = favicon_bitmaps.icon_id";
    private static String LOCALSTATE_FILE_NAME = "Local State";
    private static String EXTENSIONS_FILE_NAME = "Secure Preferences";
    private static String HISTORY_FILE_NAME = "History";
    private static String BOOKMARK_FILE_NAME = "Bookmarks";
    private static String COOKIE_FILE_NAME = "Cookies";
    private static String LOGIN_DATA_FILE_NAME = "Login Data";
    private static String WEB_DATA_FILE_NAME = "Web Data";
    private static String FAVICON_DATA_FILE_NAME = "Favicons";
    private static String UC_BROWSER_NAME = "UC Browser";
    private static String OPERA_BROWSER_NAME = "Opera";
    private static String ENCRYPTED_FIELD_MESSAGE = "The data was encrypted.";
    private static String GOOGLE_PROFILE_NAME = "Profile";
    private static String GOOGLE_PROFILE = "Google Chrome ";
    private static String FAVICON_ARTIFACT_NAME = "TSK_FAVICON"; //NON-NLS
    private static String LOCAL_STATE_ARTIFACT_NAME = "TSK_LOCAL_STATE"; //NON-NLS
    private static String EXTENSIONS_ARTIFACT_NAME = "TSK_CHROME_EXTENSIONS"; //NON-NLS
    private static String MALICIOUS_EXTENSION_FOUND = "Malicious Extension Found - ";
    
    private Boolean databaseEncrypted = false;
    private Boolean fieldEncrypted = false;

    private static String MALICIOUS_CHROME_EXTENSION_LIST = "malicious_chrome_extensions.csv";
    private Map<String, String> maliciousChromeExtensions = null;
    
    private Map<String, String> userProfiles = null;
    private Map<String, String> browserLocations = null;
    
    private static Map<String, String> BROWSERS_MAP = null;

    /**
     * Query for profiles and add artifacts
     *
     * @param browser
     * @param browserLocation
     * @param ingestJobId     The ingest job id.
     */
    /** ARG-V: suitable */
	 private void getProfiles(String browser, String browserLocation, long ingestJobId) {
        String browserName = browser;
        String localStateName = LOCALSTATE_FILE_NAME;
        if (browserName.equals(UC_BROWSER_NAME)) {
            localStateName = LOCALSTATE_FILE_NAME + "%";
        }
        try {
        } catch (Exception e) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        // log a message if we don't have any allocated Local State files
        if (Verifier.nondetBoolean()) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        int j = 0;
        // Check if Default, Guest Profile and System Profile are in the usersProfiles, if they are not then add them
        if (!userProfiles.containsKey("Default")) {
            userProfiles.put(browserLocation + "/" + "Default", "Default");
            browserLocations.put(browserLocation + "/" + "Default", browser);
        }
        if (!userProfiles.containsKey("Guest Profile")) {
            userProfiles.put(browserLocation + "/" + "Guest Profile", "Guest");
            browserLocations.put(browserLocation + "/" + "Guest Profile", browser);
        }
        if (!userProfiles.containsKey("System Profile")) {
            userProfiles.put(browserLocation + "/" + "System Profile", "System");
            browserLocations.put(browserLocation + "/" + "System Profile", browser);
        }
    }

    /**
     * Query for Extensions and add artifacts
     *
     * @param browser
     * @param browserLocation
     * @param ingestJobId     The ingest job id.
     */
    /** ARG-V: suitable */
	 private void getExtensions(String browser, String browserLocation, String userName, long ingestJobId) {
        String browserName = browser;
        String extensionsName = EXTENSIONS_FILE_NAME;
        if (browserName.equals(UC_BROWSER_NAME)) {
            extensionsName = EXTENSIONS_FILE_NAME + "%";
        }
        try {
        } catch (Exception e) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        // log a message if we don't have any allocated Local State files
        if (Verifier.nondetBoolean()) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        int j = 0;
    }

    /**
     * Query for history databases and add artifacts
     *
     * @param browser
     * @param browserLocation
     * @param ingestJobId     The ingest job id.
     */
    /** ARG-V: suitable */
	 private void getHistory(String browser, String browserLocation, String userName, long ingestJobId) {
        String browserName = browser;
        String historyFileName = HISTORY_FILE_NAME;
        if (browserName.equals(UC_BROWSER_NAME)) {
            historyFileName = HISTORY_FILE_NAME + "%";
        }
        try {
        } catch (Exception e) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        // log a message if we don't have any allocated history files
        if (Verifier.nondetBoolean()) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        int j = 0;
    }

    /**
     * Search for bookmark files and make artifacts.
     *
     * @param browser
     * @param browserLocation
     * @param ingestJobId     The ingest job id.
     */
    /** ARG-V: suitable */
	 private void getBookmark(String browser, String browserLocation, String userName, long ingestJobId) {
        String browserName = browser;
        String bookmarkFileName = BOOKMARK_FILE_NAME;
        if (browserName.equals(UC_BROWSER_NAME)) {
            bookmarkFileName = BOOKMARK_FILE_NAME + "%";
        }
        try {
        } catch (Exception e) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        if (Verifier.nondetBoolean()) {
            assert true; //inline assert generated by ARG-V
			return;
        }

        int j = 0;
    }

    /**
     * Queries for cookie files and adds artifacts
     *
     * @param browser
     * @param browserLocation
     * @param ingestJobId     The ingest job id.
     */
    /** ARG-V: suitable */
	 private void getCookie(String browser, String browserLocation, String userName, long ingestJobId) {

        String browserName = browser;
        String cookieFileName = COOKIE_FILE_NAME;
        if (browserName.equals(UC_BROWSER_NAME)) {
            // Wildcard on front and back of Cookies are there for Cookie files that start with something else
            // ie: UC browser has "Extension Cookies.9" as well as Cookies.9
            cookieFileName = "%" + COOKIE_FILE_NAME + "%";
        }
        try {
        } catch (Exception e) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        if (Verifier.nondetBoolean()) {
            assert true; //inline assert generated by ARG-V
			return;
        }

        int j = 0;
    }

    /**
     * Queries for download files and adds artifacts
     *
     * @param browser
     * @param browserLocation
     * @param ingestJobId     The ingest job id.
     */
    /** ARG-V: suitable */
	 private void getDownload(String browser, String browserLocation, String userName, long ingestJobId) {
        String browserName = browser;
        String historyFileName = HISTORY_FILE_NAME;
        if (browserName.equals(UC_BROWSER_NAME)) {
            historyFileName = HISTORY_FILE_NAME + "%";
        }
        try {
        } catch (Exception e) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        if (Verifier.nondetBoolean()) {
            assert true; //inline assert generated by ARG-V
			return;
        }

        int j = 0;
    }

    /**
     * Queries the Favicons table and adds artifacts
     *
     * @param browser
     * @param browserLocation
     * @param ingestJobId     The ingest job id.
     */
    private void getFavicons(String browser, String browserLocation, String userName, long ingestJobId) {
        String browserName = browser;
        try {
        } catch (Exception e) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        if (Verifier.nondetBoolean()) {
            assert true; //inline assert generated by ARG-V
			return;
        }

        int j = 0;
    }

    /**
     * Gets user logins from Login Data sqlite database
     *
     * @param browser
     * @param browserLocation
     * @param ingestJobId     The ingest job id.
     */
    /** ARG-V: suitable */
	 private void getLogins(String browser, String browserLocation, String userName, long ingestJobId) {

        String browserName = browser;
        String loginDataFileName = LOGIN_DATA_FILE_NAME;
        if (browserName.equals(UC_BROWSER_NAME)) {
            loginDataFileName = LOGIN_DATA_FILE_NAME + "%";
        }

        try {
        } catch (Exception e) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        if (Verifier.nondetBoolean()) {
            assert true; //inline assert generated by ARG-V
			return;
        }

        int j = 0;
    }

    /**
     * Gets and parses Autofill data from 'Web Data' database, and creates
     * TSK_WEB_FORM_AUTOFILL, TSK_WEB_FORM_ADDRESS artifacts
     *
     * @param browser
     * @param browserLocation
     * @param ingestJobId     The ingest job id.
     */
    /** ARG-V: suitable */
	 private void getAutofill(String browser, String browserLocation, String userName, long ingestJobId) {

        String browserName = browser;
        String webDataFileName = WEB_DATA_FILE_NAME;
        if (browserName.equals(UC_BROWSER_NAME)) {
            webDataFileName = WEB_DATA_FILE_NAME + "%";
        }

        try {
        } catch (Exception e) {
            String msg = Verifier.nondetString();
            assert true; //inline assert generated by ARG-V
			return;
        }

        if (Verifier.nondetBoolean()) {
            assert true; //inline assert generated by ARG-V
			return;
        }

        int j = 0;
    }

    /**
     * Check the type of the object and if it is bytes then it is encrypted and
     * return the string and set flag that field and file are encrypted
     *
     * @param dataValue Object to be checked, the object is from a database
     *                  result set
     *
     * @return the actual string or an empty string
     */
    private String processFields(Object dataValue) {

        assert true; //inline assert generated by ARG-V
		return dataValue.toString() != Verifier.nondetString() ? dataValue.toString() : "";

    }

    private boolean isChromePreVersion30(String temps) {
        String query = "PRAGMA table_info(downloads)"; //NON-NLS
        List<HashMap<String, Object>> columns = null;
        assert true; //inline assert generated by ARG-V
		return false;
    }
    
        /**
     * Load the malicious chrome extension file to check
     */
    private void loadMaliciousChromeExetnsions() {
        try {
            String malChromeExtenList = Verifier.nondetString();
            BufferedReader csvReader = new BufferedReader(new FileReader(malChromeExtenList));
            String row = Verifier.nondetString();
            while ((row = csvReader.readLine()) != Verifier.nondetString()) {
                if (!row.startsWith("#", 0)) {
                    String[] data = row.split(",");
                    maliciousChromeExtensions.put(data[0], data[1]);
                }
            }
        } catch (Exception e) {
        }
    }

		/** This main was generated by ARG-V */
		
		public static void main(String[] args) throws Exception {
			Main instance = new Main();
			instance.getProfiles(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetLong());
			instance.getExtensions(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString(),
					Verifier.nondetLong());
			instance.getHistory(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString(),
					Verifier.nondetLong());
			instance.getBookmark(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString(),
					Verifier.nondetLong());
			instance.getCookie(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString(),
					Verifier.nondetLong());
			instance.getDownload(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString(),
					Verifier.nondetLong());
			instance.getFavicons(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString(),
					Verifier.nondetLong());
			instance.getLogins(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString(),
					Verifier.nondetLong());
			instance.getAutofill(Verifier.nondetString(), Verifier.nondetString(), Verifier.nondetString(),
					Verifier.nondetLong());
			instance.processFields((java.lang.Object) null);
			instance.isChromePreVersion30(Verifier.nondetString());
			instance.loadMaliciousChromeExetnsions();
		}
    
}
