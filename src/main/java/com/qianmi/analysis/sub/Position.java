package com.qianmi.analysis.sub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 位置，包含方向和长度
 * Created by liuzhaoming on 2018/7/23.
 */
public class Position {
    public static final String DIRECTION_POST = "postfix";

    public static final String DIRECTION_PRE = "prefix";

    public static final int DEFAULT_SIZE = 4;

    public static final Position DEFAULT = new Position(DIRECTION_POST, DEFAULT_SIZE);

    /**
     * 方向
     */
    private String direction;

    /**
     * 长度
     */
    private int size;

    public Position(String direction, int size) {
        this.direction = direction;
        this.size = size;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Position{" +
                "direction='" + direction + '\'' +
                ", size=" + size +
                '}';
    }

    /**
     * 从字符串中解析位置对象，字符串的格式为“postfix:4;postfix:6;prefix:4”
     *
     * @param positionStr 字符串对象
     * @return 位置对象列表
     */
    public static List<Position> parse(String positionStr) {
        if (null == positionStr || positionStr.length() == 0) {
            return Collections.singletonList(DEFAULT);
        }

        try {
            String[] temps = positionStr.split(";");
            List<Position> positions = new ArrayList<>();
            for (String temp : temps) {
                String[] directionAndSize = temp.split(":");
                String direction = directionAndSize[0];
                if (!DIRECTION_POST.equals(direction) && !DIRECTION_PRE.equals(direction)) {
                    direction = DIRECTION_POST;
                }

                int size = Integer.valueOf(directionAndSize[1]);
                if (size <= 0) {
                    size = DEFAULT_SIZE;
                }

                positions.add(new Position(direction, size));
            }
            return positions;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.singletonList(DEFAULT);
    }
}
