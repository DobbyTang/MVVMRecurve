package com.prettifier.pretty.helper;


import androidx.annotation.NonNull;

/**
 * Created by Kosh on 25 Dec 2016, 9:12 PM
 */

public class PrettifyHelper {

    @NonNull
    private static String getHtmlContent(@NonNull String css, @NonNull String text, boolean wrap, boolean isDark) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <link rel=\"stylesheet\" href=\"./highlight/styles/" + css + "\">\n" +
                "" + (!wrap ? "<meta name=\"viewport\" content=\"width=device-width, height=device-height, " +
                "initial-scale=.5,user-scalable=yes\"/>\n" : "") + "" +
                LINE_NO_CSS + "\n" +
                "    " + (wrap ? WRAPPED_STYLE : "") + "\n" +
                "<script src=\"./highlight/js/prettify.js\"></script>\n" +
                "<script src=\"./highlight/js/prettify_line_number.js\"></script>\n" +
                "</head>\n" +
                "<body style=\"" + (isDark && textTooLarge(text) ? "color:white;" : "") + "\">\n" +
                "<pre><code>" + text + "</code></pre>\n" +
                "<script>" + (textTooLarge(text) ? "" : "hljs.initHighlightingOnLoad();\nhljs.initLineNumbersOnLoad();") + "</script>\n" +
                "<script src=\"./highlight/js/scrollto.js\"></script>\n" +
                "\n<script src=\"./md/intercept-touch.js\"></script>\n" +
                "</body>\n" +
                "</html>";
    }

    @NonNull private static final String WRAPPED_STYLE =
            "<style>\n " +
                    "td.hljs-ln-code {\n" +
                    "    word-wrap: break-word !important;\n" +
                    "    word-break: break-all  !important;\n" +
                    "    white-space: pre-wrap  !important;\n" +
                    "}" +
                    "img {\n" +
                    "    max-width: 100% !important;\n" +
                    "}\n" +
                    "ol {\n" +
                    "    margin-left: 0 !important;\n" +
                    "    padding-left: 6px !important;\n" +
                    "}\n" +
                    "ol li {\n" +
                    "    margin-left: 0  !important;\n" +
                    "    padding-left: 0  !important;\n" +
                    "    text-indent: -12px !important;\n" +
                    "}" +
                    "</style>";

    private static final String LINE_NO_CSS = "<style>\n " +
            "td.hljs-ln-numbers {\n" +
            "    -webkit-touch-callout: none;\n" +
            "    -webkit-user-select: none;\n" +
            "    -khtml-user-select: none;\n" +
            "    -moz-user-select: none;\n" +
            "    -ms-user-select: none;\n" +
            "    user-select: none;\n" +
            "    text-align: center;\n" +
            "    color: #ccc;\n" +
            "    border-right: 1px solid #CCC;\n" +
            "    vertical-align: top;\n" +
            "    padding-right: 3px !important;\n" +
            "}\n" +
            "\n" +
            ".hljs-ln-line {\n" +
            "    margin-left: 6px !important;\n" +
            "}\n" +
            "</style>";

    @NonNull public static String generateContent(@NonNull String source, String theme) {
        return generateContent(theme, getFormattedSource(source), false, false);
    }

    @NonNull public static String generateContent(@NonNull String source, boolean isDark, boolean wrap) {
        return generateContent("prettify.css", getFormattedSource(source),  wrap, isDark);
    }

    @NonNull public static String generateContent( String theme,@NonNull String source, boolean isDark, boolean wrap){
        return getHtmlContent(theme, getFormattedSource(source), wrap, isDark);
    }

    @NonNull private static String getFormattedSource(@NonNull String source) {
        return source.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    private static boolean textTooLarge(@NonNull String text) {
        return text.length() > 304800;//>roughly 300kb ? disable highlighting to avoid crash.
    }

}
