public class StringBuilderSplitExample {
    public static void main(String[] args) {
        // 使用 StringBuilder 创建一个字符串
        StringBuilder input = new StringBuilder("这是一个例子，包含逗号。还有括号（和）");

        // 将 StringBuilder 转换为 String
        String str = input.toString();

        // 使用 while 循环，确保只有在字符串长度大于10时才执行分割操作
        while (str.length() > 10) {
            // 使用正则表达式分割字符串，按标点符号 (，。）（)）进行截取
            String[] parts = str.split("[，。（）]");

            // 遍历分割后的部分，检查长度并执行相应操作
            boolean foundSmallPart = false; // 用于标记是否遇到小于10的部分
            for (String part : parts) {
                // 如果部分的长度小于10，则执行添加语句，并跳出处理
                if (part.length() < 10) {
                    System.out.println("添加语句: " + part);
                    foundSmallPart = true; // 标记为已找到小于10的部分
                    break; // 停止分割，因为后面的部分不需要再分割了
                }
            }

            // 如果找到了小于10的部分，就停止整个循环
            if (foundSmallPart) {
                break;
            }

            // 如果没有找到小于10的部分，可以更新 str 进行下一次循环
            // 例如：删除已处理的部分，这里只是示例
            str = str.substring(str.indexOf(parts[parts.length - 1]) + parts[parts.length - 1].length());
        }

        System.out.println("最终字符串处理完成");
    }
}
