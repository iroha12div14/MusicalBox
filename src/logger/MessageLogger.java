package logger;

/**
 * デバッグ用メッセージをコンソールに出力する。
 */
public class MessageLogger {
    /**
     * クラスを明示しつつログを出力する
     * @param obj メッセージ元のクラス（thisでいい）
     * @param msg 表示メッセージ
     * @param tab インデント
     */
    public static void printMessage(Object obj, String msg, int tab) {
        String[] classSplitName = obj.getClass().getName().split("\\.");
        String className = classSplitName[classSplitName.length - 1];
        System.out.println(msg + "\t".repeat(tab) + "@" + className);
    }

    /**
     * クラスを明示しつつログを出力する（インデント無し）
     * @param obj メッセージ元のクラス（thisでいい）
     * @param msg 表示メッセージ
     */
    public static void printMessage(Object obj, String msg) {
        String[] classSplitName = obj.getClass().getName().split("\\.");
        String className = classSplitName[classSplitName.length - 1];
        System.out.println(msg + " @" + className);
    }

    // アドレスからファイル名を抽出
    public static String getFileNameFromAddress(String fileAddress) {
        if(fileAddress.contains("\\") ) {
            String[] fileAddressArr = fileAddress.split("\\\\");
            return fileAddressArr[fileAddressArr.length - 1];
        } else {
            return  fileAddress;
        }
    }
}
