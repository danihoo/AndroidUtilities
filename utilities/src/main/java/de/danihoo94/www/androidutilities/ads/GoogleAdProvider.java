package de.danihoo94.www.androidutilities.ads;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class GoogleAdProvider implements AdProvider {

    private final FragmentActivity activity;

    private ConsentForm consentForm;
    private InterstitialAd ad;
    private long lastAd;

    private boolean loading = false;
    private boolean failed = false;

    public GoogleAdProvider(FragmentActivity activity) {
        this.activity = activity;
        setup();
    }

    private void setup() {
        // Initialize mobile ads
        MobileAds.initialize(activity, initializationStatus -> {
        });

        // Set ad configuration
        List<String> testDevices = new ArrayList<>();
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);

        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);

        // Consent
        checkAdConsent();

        loadInterstitial();
    }

    public void showAd() {
        if (ad == null) {
            loadInterstitial();
        } else {
            long time = Calendar.getInstance().getTimeInMillis();
            if (lastAd + (getFrequencyCappingMinutes() * 60000) <= Calendar.getInstance().getTimeInMillis()) {
                ad.show(this.activity);
                lastAd = time;
                ad = null;

                // load next ad
                loadInterstitial();
            }
        }
    }

    void loadInterstitial() {
        if (ad == null && !loading) {
            loading = true;

            Bundle extras = new Bundle();

            ConsentInformation consentInformation = ConsentInformation.getInstance(activity);
            if (failed || consentInformation.getConsentStatus().equals(ConsentStatus.NON_PERSONALIZED)) {
                extras.putString("npa", "1");
            }

            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();

            InterstitialAd.load(activity, activity.getString(getAdUnit()), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    ad = interstitialAd;
                    loading = false;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e("error", "Failed to load ad: " + loadAdError.toString());
                    loading = false;
                }
            });
        }
    }

    void checkAdConsent() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(activity);
        consentInformation.requestConsentInfoUpdate(new String[]{activity.getString(getPublisherId())}, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                if (consentStatus == ConsentStatus.UNKNOWN) {
                    try {
                        displayConsentForm();
                    } catch (Exception e) {
                        failed = true;
                    }
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                failed = true;
            }
        });
    }

    private void displayConsentForm() {

        consentForm = new ConsentForm.Builder(activity, getAppsPrivacyPolicy()).withListener(new ConsentFormListener() {
            @Override
            public void onConsentFormLoaded() {
                // Display Consent Form When Its Loaded
                consentForm.show();
            }

            @Override
            public void onConsentFormError(String errorDescription) {
            }

            @Override
            public void onConsentFormOpened() {

            }

            @Override
            public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
            }
        })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        consentForm.load();
    }

    private URL getAppsPrivacyPolicy() {
        URL mUrl = null;
        try {
            mUrl = new URL(activity.getString(getPrivacyPolicyUrl()));
        } catch (MalformedURLException ignored) {
        }
        return mUrl;
    }

    protected abstract int getFrequencyCappingMinutes();

    @StringRes
    protected abstract int getAdUnit();

    @StringRes
    protected abstract int getPublisherId();

    @StringRes
    protected abstract int getPrivacyPolicyUrl();
}
