package cn.poco.pie;

/**
 * Date  : 2016/11/10
 * Author: MarkChan
 * Desc  :
 */
public interface PieStatusCode {

    int SUCCESS = 200;
    int CANCEL = 500;
    int ERROR = 404;

    int ERR_EXCEPTION = -1;
    int ERR_EMPTY_ATY_OR_FINISHING = -2;
    int ERR_SHARE_EMPTY_CONTENT = -3;
    int ERR_NOT_CONFIG = -4;
    int ERR_SHARE_PARAM_INVALID = -5;
    int ERR_NOT_INSTALL = -6;
    int ERR_SHARE_DOWNLOAD_IMG_FAILED = -7;
    int ERR_NOT_SUPPORT_SHARE = -8;
    int ERR_NOT_HANDLER = -9;
    int ERR_REQUEST = -10;
}
