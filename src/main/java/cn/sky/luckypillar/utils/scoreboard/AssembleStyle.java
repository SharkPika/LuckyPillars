package cn.sky.luckypillar.utils.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter
@AllArgsConstructor
public enum AssembleStyle {

    KOHI(true, 15),
    VIPER(true, -1),
    MODERN(false, 1);

    private final boolean descending;
    private final int startNumber;

    /**
     * Assemble Style.
     *
     * @param descending  whether the positions are going down or up.
     * @param startNumber from where to loop from.
     */
}
