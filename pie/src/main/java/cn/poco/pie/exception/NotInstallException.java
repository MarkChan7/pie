package cn.poco.pie.exception;

import cn.poco.pie.PieStatusCode;

/**
 * Date  : 2016/11/11
 * Author: MarkChan
 * Desc  :
 */
public class NotInstallException extends PieException {

    public NotInstallException(String message) {
        super(message);
        setCode(PieStatusCode.ERR_NOT_INSTALL);
    }
}
