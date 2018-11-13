package com.zxxkj.service;


import javax.servlet.http.HttpServletRequest;

import exception.FSException;
public interface FreeSwitchService {

    String freeSwitchResp(HttpServletRequest request) throws FSException;

    String freeSwitchRecordCall(HttpServletRequest request) throws FSException;

    String freeSwitchFirstPlayBack(HttpServletRequest request) throws FSException;

    String freeSwitchPlayBack(HttpServletRequest request) throws FSException;

    void freeSwitchUploadRecordCallFile( HttpServletRequest request) throws FSException;

    void freeSwitchUploadRecordChunkAckFile(HttpServletRequest request) throws FSException;

    String freeswitchSilenceStream(HttpServletRequest request) throws FSException;

    String freeswitchSilenceStreamBusy(HttpServletRequest request) throws FSException;

    String freeswitchHangUp(HttpServletRequest request) throws FSException;

    String freeswitchBusy(HttpServletRequest request) throws FSException;

}
