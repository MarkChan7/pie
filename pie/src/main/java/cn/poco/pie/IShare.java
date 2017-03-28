package cn.poco.pie;

import cn.poco.pie.exception.PieException;
import cn.poco.pie.media.PieAudio;
import cn.poco.pie.media.PieImage;
import cn.poco.pie.media.PieVideo;
import cn.poco.pie.media.PieWebpage;

/**
 * Date  : 2016/11/11
 * Author: MarkChan
 * Desc  :
 */
public interface IShare {

    void shareText(PieContent pieContent) throws PieException;

    void shareImage(PieContent pieContent, PieImage pieImage) throws PieException;

    void shareWebpage(PieContent pieContent, PieWebpage pieWebpage) throws PieException;

    void shareAudio(PieContent pieContent, PieAudio pieAudio) throws PieException;

    void shareVideo(PieContent pieContent, PieVideo pieVideo) throws PieException;
}
