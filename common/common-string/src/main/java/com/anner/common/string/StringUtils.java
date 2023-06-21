package com.anner.common.string;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by anner on 2023/3/15
 */
public class StringUtils {
    /**
     * 空字符串
     */
    public static final String EMPTY = "";

    /**
     * 空白字符串
     */
    public static final String BLANK = " ";

    private StringUtils() {
    }

    /**
     * 生成一个非{@code null}字符串.
     *
     * <p>
     * 如果字符串非{@code null}则返回它本身，否则返回{@link StringUtils#EMPTY}
     * </p>
     *
     * @param str 字符串
     * @return 非{@code null}字符串
     * @since 0.1
     */
    @NotNull
    public static String alwaysNotNull(@Nullable String str) {
        return str == null ? StringUtils.EMPTY : str;
    }

    /**
     * 检查一个字符串是否为空字符串.
     *
     * <p>
     * 如果字符串为{@code null}或{@link StringUtils#EMPTY}则返回{@code true}
     * </p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str 被检查的字符串
     * @return 是否为空字符串
     * @since 0.1
     */
    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 检查一个字符串是否非空字符串.
     *
     * <p>
     * 如果字符串非{@code null}且非空字符串则返回{@code true}
     * </p>
     *
     * @param str 被检查的字符串
     * @return 检查结果
     * @see StringUtils#isEmpty(String)
     * @since 0.1
     */
    public static boolean isNotEmpty(@Nullable String str) {
        return !StringUtils.isEmpty(str);
    }

    /**
     * 检查一个字符串是否为空白字符串.
     *
     * <p>
     * 如果字符串为{@code null}，或长度为0，或为空白字符串则返回{@code true}
     * </p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str 被检查的字符串
     * @return 检查结果
     * @since 0.1
     */
    public static boolean isBlank(@Nullable String str) {

        int strLen;
        return (str == null || (strLen = str.length()) == 0) || isBlank(str, strLen);
    }


    /**
     * 检查一个字符串的子串是否非空白字符串.
     *
     * <p>
     * 如果字符串非{@code null}，且长度非0，且非空白字符串则返回{@code true}
     * </p>
     *
     * @param str 被检查的字符串
     * @return 检查结果
     * @see StringUtils#isBlank(String)
     * @since 0.1
     */
    public static boolean isNotBlank(String str) {

        return !StringUtils.isBlank(str);
    }

    /**
     * 如果字符串含有特定前缀，去除该前缀.
     *
     * <p>
     * 当字符串非{@code null}，前缀非{@code null}，且字符串有该前缀时，返回去除前缀的字符串，否则返回字符串本身
     * </p>
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 去除前缀后的字符串
     * @since 0.1
     */
    @Nullable
    public static String cutStringStartWith(@Nullable String str, @Nullable String prefix) {

        if (str == null) {
            return null;
        }

        if (prefix == null) {
            return str;
        }
        if (!str.startsWith(prefix)) {
            return str;
        }
        return str.substring(prefix.length());
    }

    /**
     * 如果字符串含有特定后缀，去除该后缀.
     *
     * <p>
     * 当字符串非{@code null}，后缀非{@code null}，且字符串有该后缀时，返回去除后缀的字符串，否则返回字符串本身
     * </p>
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 去除后缀后的字符串
     * @since 0.1
     */
    @Nullable
    public static String cutStringEndWith(@Nullable String str, @Nullable String suffix) {

        if (str == null) {
            return null;
        }

        if (suffix == null) {
            return str;
        }

        if (!str.endsWith(suffix)) {
            return str;
        }
        int location = str.indexOf(suffix);
        return str.substring(0, location);
    }

    /**
     * 去除字符串首尾的空白.
     *
     * <p>
     * 如果字符串非{@code null}，返回去除首尾空白之后的字符串，否则返回{@code null}
     * </p>
     *
     * <pre>
     * StringUtils.trim(null)          = null
     * StringUtils.trim("")            = ""
     * StringUtils.trim("     ")       = ""
     * StringUtils.trim("abc")         = "abc"
     * StringUtils.trim("    abc    ") = "abc"
     * StringUtils.trim("    ab c    ") = "ab c"
     * </pre>
     *
     * @param str 待处理的字符串
     * @return 处理后的字符串
     * @since 0.1
     */
    @Nullable
    public static String trim(@Nullable String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 去除字符串中的空白，如果结果为空字符串则返回{@code null}.
     *
     * <p>
     * 如果字符串非{@code null}，且去除空白后非空字符串，则返回去除空白后的结果，否则返回{@code null}
     * </p>
     *
     * <pre>
     * StringUtils.trim(null)          = null
     * StringUtils.trim("")            = null
     * StringUtils.trim("     ")       = null
     * StringUtils.trim("abc")         = "abc"
     * StringUtils.trim("    abc    ") = "abc"
     * </pre>
     *
     * @param str 待处理的字符串
     * @return 处理后的字符串
     * @see StringUtils#isEmpty(String)
     * @since 0.1
     */
    public static String trimToNull(String str) {

        String ts = trim(str);
        return isEmpty(ts) ? null : ts;
    }

    /**
     * 如果字符串不含特定前缀，添加该前缀.
     *
     * <p>
     * 如果字符串为{@code null}，返回前缀<br>
     * 如果字符串非{@code null}且没有该前缀，返回加上前缀后的字符串，否则返回字符串本身
     * </p>
     *
     * <pre>
     * StringUtils.perfectStart(null, "a")  = "a"
     * StringUtils.perfectStart("b", "a")   = "ab"
     * StringUtils.perfectStart("ab", "a")  = "ab"
     * </pre>
     *
     * @param str    待处理的字符串
     * @param prefix 前缀
     * @return 处理后的字符串
     * @since 0.1
     */
    @NotNull
    public static String perfectStart(@Nullable String str, @NotNull String prefix) {

        if (str == null) {
            return prefix;
        }
        return str.startsWith(prefix) ? str : (prefix + str);
    }

    /**
     * 如果字符串不含特定后缀，添加该后缀.
     *
     * <p>
     * 如果字符串为{@code null}，返回后缀<br>
     * 如果字符串非{@code null}且没有该后缀，返回加上后缀后的字符串，否则返回字符串本身
     * </p>
     *
     * <pre>
     * StringUtils.perfectEnd(null, "b")  = "b"
     * StringUtils.perfectEnd("a", "b")   = "ab"
     * StringUtils.perfectEnd("ab", "b")  = "ab"
     * </pre>
     *
     * @param str    待处理的字符串
     * @param suffix 后缀
     * @return 处理后的字符串
     * @since 0.1
     */
    @NotNull
    public static String perfectEnd(@Nullable String str, @NotNull String suffix) {

        if (str == null) {
            return suffix;
        }
        return str.endsWith(suffix) ? str : (str + suffix);
    }

    /**
     * 使字符串具有特定词缀作为前后缀.
     *
     * <p>
     * 如果字符串为{@code null}，返回该前后缀<br>
     * 如果字符串非{@code null}且没有该前后缀作为前缀或后缀，返回加上该前后缀后的字符串，否则返回字符串本身
     * </p>
     *
     * <pre>
     * StringUtils.perfectSurround(null, "b")  = "b"
     * StringUtils.perfectSurround("a", "b")   = "bab"
     * StringUtils.perfectSurround("ab", "b")  = "bab"
     * StringUtils.perfectSurround("ba", "b")  = "bab"
     * StringUtils.perfectSurround("bab", "b")  = "bab"
     * </pre>
     *
     * @param str   待处理的字符串
     * @param affix 前后缀
     * @return 处理后的字符串
     * @since 0.1
     */
    @NotNull
    public static String perfectSurround(@Nullable String str, @NotNull String affix) {

        if (str == null) {
            return affix;
        }
        str = str.endsWith(affix) ? str : (str + affix);
        str = str.startsWith(affix) ? str : (affix + str);

        return str;
    }

    /**
     * 获取字符串的长度.
     *
     * <p>
     * 如果字符串为{@code null}，返回0，否则返回字符串的长度
     * </p>
     *
     * @param str 字符串
     * @return 字符串的长度
     * @since 0.1
     */
    public static int getLength(@Nullable String str) {

        return str == null ? 0 : str.length();
    }

    /**
     * 判断两个字符串去除可能存在的前后缀后是否相同.
     *
     * <pre>
     * StringUtils.equalsIgnore("b", "b", "a")      = true
     * StringUtils.equalsIgnore("ab", "b", "a")     = true
     * StringUtils.equalsIgnore("b", "ba", "a")     = true
     * StringUtils.equalsIgnore("ab", "ba", "a")    = true
     * StringUtils.equalsIgnore("aba", "ba", "a")   = true
     * StringUtils.equalsIgnore("ba", "aba", "a")   = true
     * StringUtils.equalsIgnore("aba", "aba", "a")  = true
     * </pre>
     *
     * @param str1  待比较的字符串1
     * @param str2  待比较的字符串2
     * @param affix 前后缀
     * @return 两个字符串去除可能存在的前后缀后是否相同
     * @throws RuntimeException 如果str1或str2为{@code null}
     * @since 0.1
     */
    public static boolean equalsIgnore(@Nullable String str1, @Nullable String str2, @NotNull String affix) {

        if (str1 == null || str2 == null) {
            throw new RuntimeException("null path");
        }
        return equals(str1, str2) || perfectStart(perfectEnd(str1, affix), affix).equals(perfectStart(perfectEnd(str2, affix), affix));
    }

    /**
     * 判断字符串是否含有特定子串.
     *
     * <p>
     * 如果字符串为{@code null}，则返回{@code false}<br>
     * 如果字符串非{@code null}，返回字符串是否含有该子串
     * </p>
     *
     * @param str    字符串
     * @param subStr 子串
     * @return 字符串是否含有该子串
     * @since 0.1
     */
    public static boolean contains(@Nullable String str, @NotNull String subStr) {
        return str != null && str.contains(subStr);
    }

    /**
     * 获取字符串以{@code \r\n}作为分隔符的{@code StringTokenizer}.
     *
     * @param str 字符串
     * @return StringTokenizer {@code StringTokenizer}
     * @since 0.1
     */
    public static StringTokenizer text2StringTokenizer(@NotNull String str) {
        return new StringTokenizer(str, "\r\n");
    }

    /**
     * 使用特定分隔符拼接字符串数组.
     *
     * <p>
     * 如果字符串数组为{@code null}，返回{@code null}<br>
     * 如果字符串数组长度为0， 返回{@link StringUtils#EMPTY}<br>
     * 如果分隔符为{@code null}，将使用"null"作为分隔符
     * </p>
     *
     * <pre>
     * StringUtils.join(",", null)                      = null
     * StringUtils.join(null, new String[]{"a", "b"})   = "anullb"
     * StringUtils.join(",", new String[]{"a", "b"})    = "a","b"
     * </pre>
     *
     * @param separator 分隔符
     * @param strings   字符串数组
     * @return 拼接成的字符串
     * @since 0.1
     */
    @Nullable
    public static String join(@Nullable String separator, @Nullable String[] strings) {

        // TODO 不影响现有逻辑的前提下处理strings长度为0的情况
        if (strings == null) {
            return null;
        }
        int length = strings.length;
        if (length == 0) {
            return StringUtils.EMPTY;
        }
        StringBuffer buf = new StringBuffer(length * strings[0].length())
                .append(strings[0]);
        for (int i = 1; i < length; i++) {
            buf.append(separator).append(strings[i]);
        }
        return buf.toString();
    }

    /**
     * 使用0~9替换字符串中的A~J.
     *
     * @param xmlDesignerVersion 字符串
     * @return 替换后的字符串
     * @since 0.1
     */
    public static String parseVersion(String xmlDesignerVersion) {

        xmlDesignerVersion = xmlDesignerVersion.replace('A', '0');
        xmlDesignerVersion = xmlDesignerVersion.replace('B', '1');
        xmlDesignerVersion = xmlDesignerVersion.replace('C', '2');
        xmlDesignerVersion = xmlDesignerVersion.replace('D', '3');
        xmlDesignerVersion = xmlDesignerVersion.replace('E', '4');
        xmlDesignerVersion = xmlDesignerVersion.replace('F', '5');
        xmlDesignerVersion = xmlDesignerVersion.replace('G', '6');
        xmlDesignerVersion = xmlDesignerVersion.replace('H', '7');
        xmlDesignerVersion = xmlDesignerVersion.replace('I', '8');
        xmlDesignerVersion = xmlDesignerVersion.replace('J', '9');
        return xmlDesignerVersion;
    }

    /**
     * 判断字符串是否是数组格式.
     *
     * <p>
     * 如果字符串为{@code null}，返回{@code false}<br>
     * 如果字符串非{@code null}，且以[开始、以]结束，或以[[开始、以]]结束，返回{@code true}
     * </p>
     *
     * @param str 被检查的字符串
     * @return 字符串是否是数组格式
     * @since 0.1
     */
    public static boolean isArrayType(@Nullable String str) {

        return str != null && ((str.startsWith("[") && str.endsWith("]")) || (str.startsWith("[[") && str.endsWith("]]")));
    }

    /**
     * 将特定形式的字符串转换为字符串数组.
     *
     * <pre>
     * StringUtils.stringToArray("[["a","b"],["c","d"]]")   = [["a","b"],["c","d"]]
     * StringUtils.stringToArray("a,b;c,d")                 = [["a","b"],["c","d"]]
     * </pre>
     *
     * @param str 字符串
     * @return 字符串数组
     * @since 0.1
     */
    @NotNull
    public static String[][] stringToArray(@NotNull String str) {

        if (isArrayType(str)) {
            //[["华东","江苏"],["华东","上海"]] 或者 ["华东","江苏"]
            str = str.replaceAll("\"", "");
            if (str.startsWith("[[") && str.endsWith("]]")) {
                String[] temp = (str.substring(2, str.length() - 2)).split("],\\[");
                String[][] strs = new String[temp.length][];
                for (int i = 0; i < strs.length; i++) {
                    strs[i] = temp[i].split(",");
                }
                return strs;
            } else {
                String[][] strs = new String[1][];
                strs[0] = str.substring(1, str.length() - 1).split(",");
                return strs;
            }
        } else {
            //华东,江苏;华东,上海
            String[] temp = str.split(";");
            String[][] strs = new String[temp.length][];
            for (int i = 0; i < strs.length; i++) {
                strs[i] = temp[i].split(",");
            }
            return strs;
        }
    }

    /**
     * 根据字节数截取字符串.
     *
     * <p>
     * 如果原始字符串为空白字符串或截取字节数为0，返回{@link StringUtils#EMPTY}
     * </p>
     *
     * @param originalString 原始字符串
     * @param charset        编码方式
     * @param byteLength     字节数
     * @return 截取后的字符串
     * @throws UnsupportedEncodingException 不支持该编码方式
     * @since 0.1
     */
    @NotNull
    public static String subStringByByteLength(@Nullable String originalString, @NotNull String charset, int byteLength) throws UnsupportedEncodingException {

        if (StringUtils.isBlank(originalString) || byteLength <= 0) {
            return StringUtils.EMPTY;
        }
        char[] chars = originalString.toCharArray();
        int length = 0, index = chars.length;
        for (int i = 0; i < chars.length; i++) {
            final int len = String.valueOf(chars[i]).getBytes(charset).length + length;
            if (len <= byteLength) {
                length = len;
            } else {
                index = i;
                break;
            }
        }
        return String.valueOf(chars, 0, index);
    }

    /**
     * 比较两个字符串是否相等.
     *
     * <p>
     * 如果两个字符串中有任意一个为{@code null}，返回{@code false}<br>
     * 如果两个字符串均为{@code null}，返回{@code true}
     * </p>
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 字符串1和字符串2是否相等
     * @since 0.1
     */
    public static boolean equals(@Nullable String str1, @Nullable String str2) {

        if (str1 == str2) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    /**
     * 比较两个字符串是否相等，忽略大小写.
     *
     * <p>
     * 如果两个字符串有一个为{@code null}，返回{@code false}<br>
     * 如果两个字符串均为{@code null}，返回{@code true}
     * </p>
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 忽略大小写，字符串1和字符串2是否相等
     * @since 0.1
     */
    public static boolean equalsIgnoreCase(@Nullable String str1, @Nullable String str2) {

        if (str1 == null) {
            return str2 == null;
        }
        return str2 != null && str1.equalsIgnoreCase(str2);
    }

    /**
     * 在字符串末尾填充空格至指定长度.
     *
     * <p>
     * 如果要填充至的长度小于字符串长度，返回字符串本身
     * </p>
     *
     * @param str  待填充的字符串
     * @param size 填充至长度
     * @return 填充后的字符串
     * @since 0.1
     */
    @NotNull
    public static String rightPad(@NotNull String str, int size) {

        int len = size - str.length();
        if (len <= 0) {
            return str;
        }
        char[] spaces = new char[len];
        Arrays.fill(spaces, ' ');
        return str + String.valueOf(spaces);
    }

//    /**
//     * 格式化字符串.
//     *
//     * <p>
//     * 使用对象填充字符串模式中的占位符，并返回格式化后的字符串<br>
//     * 字符串模式为{@code null}时，返回{@code null}
//     * </p>
//     *
//     * <pre>
//     * StringUtils.messageFormat("a{}b{}c", "+", "-")   = "a+b-c"
//     * </pre>
//     *
//     * @param messagePattern 字符串模式，使用{}作为占位符
//     * @param argArray       占位符对应的对象数组
//     * @return 格式化后的字符串
//     * @since 0.1
//     */
//    @Nullable
//    public static String messageFormat(@Nullable final String messagePattern, Object... argArray) {
//
//        MessageFormatter.FormattingTuple tuple = MessageFormatter.arrayFormat(messagePattern, argArray);
//        return tuple.getMessage();
//    }

    /**
     * 替换字符串中，最后一个出现的子串.
     *
     * <p>
     * 如果字符串不含有子串，返回字符串本身
     * </p>
     *
     * <pre>
     * StringUtils.replaceLast("aaa", "a", "b") = "aab"
     * </pre>
     *
     * @param string  字符串
     * @param find    待替换子串
     * @param replace 替换子串
     * @return 替换后的字符串
     * @since 0.1
     */
    @NotNull
    public static String replaceLast(@NotNull String string, @NotNull String find, @NotNull String replace) {
        int lastIndex = string.lastIndexOf(find);

        if (lastIndex == -1) {
            return string;
        }

        String beginString = string.substring(0, lastIndex);
        String endString = string.substring(lastIndex + find.length());

        return beginString + replace + endString;
    }

    /**
     * 检查一个字符串的子串是否为空白字符串.
     *
     * @param str    被检查的字符串
     * @param strLen 子串长度
     * @return 是否为空白字符串
     * @since 0.1
     */
    private static boolean isBlank(@NotNull String str, int strLen) {
        // for JIT
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }
}
