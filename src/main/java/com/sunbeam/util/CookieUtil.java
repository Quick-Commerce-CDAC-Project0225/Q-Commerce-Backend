package com.sunbeam.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public final class CookieUtil {
    private CookieUtil() {}

    // While you’re on HTTP (AWS IP), keep secure=false
    private static final boolean DEFAULT_SECURE = false;

    /** Existing signature you already use */
    public static void writeAuthCookies(HttpServletResponse res, String accessToken, String refreshToken) {
        writeAuthCookies(res, accessToken, refreshToken, DEFAULT_SECURE);
    }

    /** Overload with explicit secure flag (use secure=true only after HTTPS) */
    public static void writeAuthCookies(HttpServletResponse res, String accessToken, String refreshToken, boolean secure) {
        String sameSite = secure ? "None" : "Lax";

        ResponseCookie access = ResponseCookie.from("access_token", accessToken)
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .path("/")
            .maxAge(Duration.ofHours(1))       // 1 hour
            .build();

        ResponseCookie refresh = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .path("/")
            .maxAge(Duration.ofDays(7))        // 7 days
            .build();

        res.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        res.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
    }

    /** Convenience delete using default secure flag */
    public static void deleteAuthCookies(HttpServletResponse res) {
        deleteAuthCookies(res, DEFAULT_SECURE);
    }

    /** Delete both cookies; must be written on the RESPONSE */
    public static void deleteAuthCookies(HttpServletResponse res, boolean secure) {
        String sameSite = secure ? "None" : "Lax";

        ResponseCookie accessDel = ResponseCookie.from("access_token", "")
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .path("/")
            .maxAge(0)                         // delete
            .build();

        ResponseCookie refreshDel = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .path("/")
            .maxAge(0)                         // delete
            .build();

        res.addHeader(HttpHeaders.SET_COOKIE, accessDel.toString());
        res.addHeader(HttpHeaders.SET_COOKIE, refreshDel.toString());
    }
}
