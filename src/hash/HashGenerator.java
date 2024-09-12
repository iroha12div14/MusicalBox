package hash;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// 参照元：https://qiita.com/yasushi-jp/items/ac8c7ead98794aed9905

/**
 * ハッシュ値を生成する。
 */
public class HashGenerator {
    // 各種アルゴリズム
    public static final String MD2     = "MD2";
    public static final String MD5     = "MD5";
    public static final String SHA_1   = "SHA-1";
    public static final String SHA_256 = "SHA-256";
    public static final String SHA_512 = "SHA-512";

    /**
     * ファイルのハッシュ値（文字列）を返す. （ファイルパスはルート込み）
     * @param filePath ファイルの絶対パス（Path型）
     * @param algorithmName アルゴリズム
     * @return ハッシュ値(文字列)
     */
    public static String getFileHash(Path filePath, String algorithmName) {
        byte[] hash;

        // アルゴリズムを取得
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithmName);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try (
                DigestInputStream dis = new DigestInputStream(
                        new BufferedInputStream(Files.newInputStream(filePath) ), md)
        ) {
            // ファイルの読み込み（読み込んだ中身をどうのこうのはしない）
            while(dis.read() != -1) { }

            // ハッシュ値の計算
            hash = md.digest();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // ハッシュ値（byte）を文字列に変換し返却
        StringBuilder sb = new StringBuilder();
        for(byte b : hash) {
            String hex = String.format("%02x", b);
            sb.append(hex);
        }
        return sb.toString();
    }

    // パンチカードのSHA-256だけ欲しい時はこれ
    /**
     * ファイルのSHA256を返す
     * @param filePath ファイルの絶対パス（Path型）
     * @return SHA256（文字列）
     */
    public static String getSha256(Path filePath) {
        return getFileHash(filePath, SHA_256);
    }
}
