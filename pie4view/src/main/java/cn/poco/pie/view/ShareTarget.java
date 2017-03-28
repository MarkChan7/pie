package cn.poco.pie.view;

import cn.poco.pie.SocialNetwork;

/**
 * Date  : 2016/11/10
 * Author: MarkChan
 * Desc  :
 */
public class ShareTarget {

    public SocialNetwork socialNetwork;
    public int titleResId;
    public int iconResId;

    public ShareTarget(SocialNetwork socialNetwork, int titleResId, int iconResId) {
        this.socialNetwork = socialNetwork;
        this.iconResId = iconResId;
        this.titleResId = titleResId;
    }
}
