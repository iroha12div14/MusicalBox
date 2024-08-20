package scene;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

public class SceneTemplate extends SceneBase {

    //コンストラクタ
    public SceneTemplate(Map<Integer, Object> data) {
        // 画面サイズ、FPS、キーアサインの初期化
        init(keyAssign, data);
    }

    // 描画したい内容はここ
    @Override
    protected void paintField(Graphics2D g2d) {

    }

    // 毎フレーム処理したい内容はここ
    @Override
    protected void actionField() {

    }

    // キーアサインの初期化
    private static final List<Integer> keyAssign = Arrays.asList(
            KeyEvent.VK_J, KeyEvent.VK_F
    );
}
