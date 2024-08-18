package playmusic.parts;

import font.FontUtil;

import java.awt.*;

public class PartsJudge {
    // フォント用のインスタンス
    FontUtil font = new FontUtil();

    // 判定テキスト
    private final String[] judgeText = {"PERFECT!!", "GREAT!", "GOOD", "OOPS", "MISS", "AUTO"};

    // フォント
    Font judgeFont = font.MSGothic(32, font.BOLD);
    Font comboFont = font.MSGothic(16);
    Font achievementFont = font.MSGothic(12);

    // 判定色
    private final Color[][] judgeColor = new Color[][]{
            {new Color(100, 255, 255), new Color(255, 255, 255), new Color(255, 255, 100)},
            {new Color(255, 180,  60), new Color(255, 180,  60), new Color(255, 180,  60)},
            {new Color( 90, 255,  90), new Color( 90, 255,  90), new Color( 90, 255,  90)},
            {new Color(220,  90, 255), new Color(220,  90, 255), new Color(220,  90, 255)},
            {new Color(255,  60,  90), new Color(255,  60,  90), new Color(255,  60,  90)},
            {new Color( 90,  90,  90), new Color( 90,  90,  90), new Color( 90,  90,  90)}
    };
    private final Color comboColor = new Color(255, 255, 255, 100);
    private final Color achievementColor = new Color(255, 255, 255, 100);

    // 判定文字の取得
    public String getJudgeText(int j ) {
        return judgeText[j];
    }
    public Font getJudgeFont() {
        return judgeFont;
    }
    public Font getComboFont() {
        return comboFont;
    }
    public Font getAchievementFont() {
        return achievementFont;
    }

    // 判定文字の色の取得
    public Color[] getJudgeColor(int j) {
        return judgeColor[j];
    }
    public Color getComboColor() {
        return comboColor;
    }
}
