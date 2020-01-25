package com.project.emi.eventscape.domain.post.videoPost;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.UUID;

import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.AD_TAG_URI_EXTRA;
import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.DRM_KEY_REQUEST_PROPERTIES_EXTRA;
import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.DRM_LICENSE_URL_EXTRA;
import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.DRM_MULTI_SESSION_EXTRA;
import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.DRM_SCHEME_EXTRA;
import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.DRM_SCHEME_UUID_EXTRA;
import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.EXTENSION_EXTRA;
import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.IS_LIVE_EXTRA;
import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.SUBTITLE_LANGUAGE_EXTRA;
import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.SUBTITLE_MIME_TYPE_EXTRA;
import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.SUBTITLE_URI_EXTRA;
import static com.project.emi.eventscape.domain.post.videoPost.VideoPost.URI_EXTRA;

/* package */ abstract class Sample {

    @Nullable
    public final String name;

    public Sample(String name) {
        this.name = name;
    }

    public static Sample createFromIntent(Intent intent) {
        if (VideoPost.ACTION_VIEW_LIST.equals(intent.getAction())) {
            ArrayList<String> intentUris = new ArrayList<>();
            int index = 0;
            while (intent.hasExtra(URI_EXTRA + "_" + index)) {
                intentUris.add(intent.getStringExtra(URI_EXTRA + "_" + index));
                index++;
            }
            UriSample[] children = new UriSample[intentUris.size()];
            for (int i = 0; i < children.length; i++) {
                Uri uri = Uri.parse(intentUris.get(i));
                children[i] = UriSample.createFromIntent(uri, intent, /* extrasKeySuffix= */ "_" + i);
            }
            return new PlaylistSample(/* name= */ null, children);
        } else {
            return UriSample.createFromIntent(intent.getData(), intent, /* extrasKeySuffix= */ "");
        }
    }

    public abstract void addToIntent(Intent intent);

    public static final class UriSample extends Sample {

        public final Uri uri;
        public final String extension;
        public final boolean isLive;
        public final DrmInfo drmInfo;
        public final Uri adTagUri;
        @Nullable
        public final String sphericalStereoMode;
        @Nullable
        SubtitleInfo subtitleInfo;
        public UriSample(
                String name,
                Uri uri,
                String extension,
                boolean isLive,
                DrmInfo drmInfo,
                Uri adTagUri,
                @Nullable String sphericalStereoMode,
                @Nullable SubtitleInfo subtitleInfo) {
            super(name);
            this.uri = uri;
            this.extension = extension;
            this.isLive = isLive;
            this.drmInfo = drmInfo;
            this.adTagUri = adTagUri;
            this.sphericalStereoMode = sphericalStereoMode;
            this.subtitleInfo = subtitleInfo;
        }

        public static UriSample createFromIntent(Uri uri, Intent intent, String extrasKeySuffix) {
            String extension = intent.getStringExtra(EXTENSION_EXTRA + extrasKeySuffix);
            String adsTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA + extrasKeySuffix);
            boolean isLive =
                    intent.getBooleanExtra(IS_LIVE_EXTRA + extrasKeySuffix, /* defaultValue= */ false);
            Uri adTagUri = adsTagUriString != null ? Uri.parse(adsTagUriString) : null;
            return new UriSample(
                    /* name= */ null,
                    uri,
                    extension,
                    isLive,
                    DrmInfo.createFromIntent(intent, extrasKeySuffix),
                    adTagUri,
                    /* sphericalStereoMode= */ null,
                    SubtitleInfo.createFromIntent(intent, extrasKeySuffix));
        }

        @Override
        public void addToIntent(Intent intent) {
            intent.setAction(VideoPost.ACTION_VIEW).setData(uri);
            intent.putExtra(VideoPost.IS_LIVE_EXTRA, isLive);
            intent.putExtra(VideoPost.SPHERICAL_STEREO_MODE_EXTRA, sphericalStereoMode);
            addPlayerConfigToIntent(intent, /* extrasKeySuffix= */ "");
        }

        public void addToPlaylistIntent(Intent intent, String extrasKeySuffix) {
            intent.putExtra(VideoPost.URI_EXTRA + extrasKeySuffix, uri.toString());
            intent.putExtra(VideoPost.IS_LIVE_EXTRA + extrasKeySuffix, isLive);
            addPlayerConfigToIntent(intent, extrasKeySuffix);
        }

        private void addPlayerConfigToIntent(Intent intent, String extrasKeySuffix) {
            intent
                    .putExtra(EXTENSION_EXTRA + extrasKeySuffix, extension)
                    .putExtra(
                            AD_TAG_URI_EXTRA + extrasKeySuffix, adTagUri != null ? adTagUri.toString() : null);
            if (drmInfo != null) {
                drmInfo.addToIntent(intent, extrasKeySuffix);
            }
            if (subtitleInfo != null) {
                subtitleInfo.addToIntent(intent, extrasKeySuffix);
            }
        }
    }

    public static final class PlaylistSample extends Sample {

        public final UriSample[] children;

        public PlaylistSample(String name, UriSample... children) {
            super(name);
            this.children = children;
        }

        @Override
        public void addToIntent(Intent intent) {
            intent.setAction(VideoPost.ACTION_VIEW_LIST);
            for (int i = 0; i < children.length; i++) {
                children[i].addToPlaylistIntent(intent, /* extrasKeySuffix= */ "_" + i);
            }
        }
    }

    public static final class DrmInfo {

        public final UUID drmScheme;
        public final String drmLicenseUrl;
        public final String[] drmKeyRequestProperties;
        public final boolean drmMultiSession;
        public DrmInfo(
                UUID drmScheme,
                String drmLicenseUrl,
                String[] drmKeyRequestProperties,
                boolean drmMultiSession) {
            this.drmScheme = drmScheme;
            this.drmLicenseUrl = drmLicenseUrl;
            this.drmKeyRequestProperties = drmKeyRequestProperties;
            this.drmMultiSession = drmMultiSession;
        }

        public static DrmInfo createFromIntent(Intent intent, String extrasKeySuffix) {
            String schemeKey = DRM_SCHEME_EXTRA + extrasKeySuffix;
            String schemeUuidKey = DRM_SCHEME_UUID_EXTRA + extrasKeySuffix;
            if (!intent.hasExtra(schemeKey) && !intent.hasExtra(schemeUuidKey)) {
                return null;
            }
            String drmSchemeExtra =
                    intent.hasExtra(schemeKey)
                            ? intent.getStringExtra(schemeKey)
                            : intent.getStringExtra(schemeUuidKey);
            UUID drmScheme = Util.getDrmUuid(drmSchemeExtra);
            String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL_EXTRA + extrasKeySuffix);
            String[] keyRequestPropertiesArray =
                    intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES_EXTRA + extrasKeySuffix);
            boolean drmMultiSession =
                    intent.getBooleanExtra(DRM_MULTI_SESSION_EXTRA + extrasKeySuffix, false);
            return new DrmInfo(drmScheme, drmLicenseUrl, keyRequestPropertiesArray, drmMultiSession);
        }

        public void addToIntent(Intent intent, String extrasKeySuffix) {
            Assertions.checkNotNull(intent);
            intent.putExtra(DRM_SCHEME_EXTRA + extrasKeySuffix, drmScheme.toString());
            intent.putExtra(DRM_LICENSE_URL_EXTRA + extrasKeySuffix, drmLicenseUrl);
            intent.putExtra(DRM_KEY_REQUEST_PROPERTIES_EXTRA + extrasKeySuffix, drmKeyRequestProperties);
            intent.putExtra(DRM_MULTI_SESSION_EXTRA + extrasKeySuffix, drmMultiSession);
        }
    }

    public static final class SubtitleInfo {

        public final Uri uri;
        public final String mimeType;
        @Nullable
        public final String language;
        public SubtitleInfo(Uri uri, String mimeType, @Nullable String language) {
            this.uri = Assertions.checkNotNull(uri);
            this.mimeType = Assertions.checkNotNull(mimeType);
            this.language = language;
        }

        @Nullable
        public static SubtitleInfo createFromIntent(Intent intent, String extrasKeySuffix) {
            if (!intent.hasExtra(SUBTITLE_URI_EXTRA + extrasKeySuffix)) {
                return null;
            }
            return new SubtitleInfo(
                    Uri.parse(intent.getStringExtra(SUBTITLE_URI_EXTRA + extrasKeySuffix)),
                    intent.getStringExtra(SUBTITLE_MIME_TYPE_EXTRA + extrasKeySuffix),
                    intent.getStringExtra(SUBTITLE_LANGUAGE_EXTRA + extrasKeySuffix));
        }

        public void addToIntent(Intent intent, String extrasKeySuffix) {
            intent.putExtra(SUBTITLE_URI_EXTRA + extrasKeySuffix, uri.toString());
            intent.putExtra(SUBTITLE_MIME_TYPE_EXTRA + extrasKeySuffix, mimeType);
            intent.putExtra(SUBTITLE_LANGUAGE_EXTRA + extrasKeySuffix, language);
        }
    }
}