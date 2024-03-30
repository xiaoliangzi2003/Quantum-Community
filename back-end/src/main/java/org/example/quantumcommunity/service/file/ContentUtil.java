package org.example.quantumcommunity.service.file;

/**
 * @author xiaol
 */
public class ContentUtil {

    public static String getSummaryFromContent(String content) {
        int length=0;
        if(content == null || content.isEmpty()) {
            return "";
        }
        if(content.length() < 100) {
            length= content.length();
        } else {
            length = 100;
        }
        //去除md特殊字符与标题，只提取正文中前100个字符作为摘要
        String summary = content.replaceAll("#+\\s+.*", "")
                .replaceAll("!\\[.*?\\]\\(.*?\\)", "")
                .replaceAll("\\*\\*.*?\\*\\*", "")
                .replaceAll("\\*.*?\\*", "")
                .replaceAll("\\[.*?\\]\\(.*?\\)", "")
                .replaceAll("`.*?`", "")
                .replaceAll("```.*?```", "")
                .replaceAll("<.*?>", "")
                .replaceAll("&.*?;", "")
                .replaceAll("\\s+", "")
                .replaceAll("\\n+", "")
                .substring(0, length);
        return summary;

    }
}
