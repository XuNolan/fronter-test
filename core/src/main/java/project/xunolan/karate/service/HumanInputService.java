package project.xunolan.karate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HumanInputService {
    /**
     * 预处理人机输入扩展词法：转换为标准 Karate 调用
     * 支持两种扩展词法：
     * 1. @Input(变量名, "提示文字", "默认值", 超时毫秒) - 文本输入
     * 2. @Confirm(变量名, "提示文字", 默认布尔值, 超时毫秒) - 是/否确认
     *
     * @param scriptData 原始脚本数据
     * @return 转换后的脚本数据
     */
    static public String preprocessHumanInput(String scriptData) {
        if (scriptData == null || scriptData.isEmpty()) {
            return scriptData;
        }

        StringBuilder result = new StringBuilder();
        String[] lines = scriptData.split("\n");
        boolean humanTypeImported = false;

        for (String line : lines) {
            String trimmedLine = line.trim();

            // 匹配 @Input(变量名, "提示", "默认值", 超时)
            if (trimmedLine.matches("^@Input\\s*\\(.*\\)\\s*$")) {
                if (!humanTypeImported) {
                    result.append("* def Human = Java.type('project.xunolan.karate.human.HumanInputGateway')\n");
                    humanTypeImported = true;
                }
                String converted = convertInputSyntax(trimmedLine);
                result.append(converted).append("\n");
            }
            // 匹配 @Confirm(变量名, "提示", 默认值, 超时)
            else if (trimmedLine.matches("^@Confirm\\s*\\(.*\\)\\s*$")) {
                if (!humanTypeImported) {
                    result.append("* def Human = Java.type('project.xunolan.karate.human.HumanInputGateway')\n");
                    humanTypeImported = true;
                }
                String converted = convertConfirmSyntax(trimmedLine);
                result.append(converted).append("\n");
            }
            else {
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }

    /**
     * 转换 @Input 词法
     * @Input(username, "请输入用户名", "admin", 60000)
     * =>
     * * def username = Human.request('请输入用户名', 'input', null, 'admin', 60000)
     */
    private static String convertInputSyntax(String line) {
        // 提取括号内容：@Input(...)
        String content = line.substring(line.indexOf('(') + 1, line.lastIndexOf(')')).trim();

        // 分割参数（简化处理，假设参数用逗号分隔）
        String[] parts = splitParameters(content);
        if (parts.length != 4) {
            log.warn("Invalid @Input syntax: {}", line);
            return "// Invalid @Input syntax: " + line;
        }

        String varName = parts[0].trim();
        String prompt = parts[1].trim();
        String defaultValue = parts[2].trim();
        String timeout = parts[3].trim();

        return String.format("* def %s = Human.request(%s, 'input', null, %s, %s)",
                varName, prompt, defaultValue, timeout);
    }

    /**
     * 转换 @Confirm 词法
     * @Confirm(needDelete, "是否删除？", false, 30000)
     * =>
     * * def needDeleteStr = Human.request('是否删除？', 'confirm', ['是','否'], 'false', 30000)
     * * def needDelete = needDeleteStr == 'true'
     */
    private static String convertConfirmSyntax(String line) {
        // 提取括号内容：@Confirm(...)
        String content = line.substring(line.indexOf('(') + 1, line.lastIndexOf(')')).trim();

        // 分割参数
        String[] parts = splitParameters(content);
        if (parts.length != 4) {
            log.warn("Invalid @Confirm syntax: {}", line);
            return "// Invalid @Confirm syntax: " + line;
        }

        String varName = parts[0].trim();
        String prompt = parts[1].trim();
        String defaultValue = parts[2].trim();
        String timeout = parts[3].trim();

        // 生成两行代码
        String tempVar = varName + "Str";
        // 若默认值是未加引号的 true/false，转换为字符串字面量
        String defaultStringParam;
        if ("true".equalsIgnoreCase(defaultValue) || "false".equalsIgnoreCase(defaultValue)) {
            defaultStringParam = "'" + defaultValue.toLowerCase() + "'";
        } else {
            defaultStringParam = defaultValue;
        }
        String line1 = String.format("* def %s = Human.request(%s, 'confirm', ['是','否'], %s, %s)",
                tempVar, prompt, defaultStringParam, timeout);
        String line2 = String.format("* def %s = %s == 'true'", varName, tempVar);

        return line1 + "\n" + line2;
    }

    /**
     * 分割参数（处理带引号的字符串）
     * 简化版本：假设参数格式为 varName, "prompt", "default", timeout
     */
    private static String[] splitParameters(String params) {
        java.util.List<String> result = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < params.length(); i++) {
            char c = params.charAt(i);

            if (c == '"' || c == '\'') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            result.add(current.toString().trim());
        }

        return result.toArray(new String[0]);
    }
}
