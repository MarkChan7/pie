package cn.poco.pie.exception;

import cn.poco.pie.PieStatusCode;

/**
 * Date  : 2016/11/11
 * Author: MarkChan
 * Desc  :
 */
public class InvalidParamException extends PieException {

    public InvalidParamException(String message) {
        super(message);
        setCode(PieStatusCode.ERR_SHARE_PARAM_INVALID);
    }
}
