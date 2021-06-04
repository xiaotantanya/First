package com.scutsehm.openplatform.service;

import com.scutsehm.openplatform.VO.Result;
import com.scutsehm.openplatform.paramTools.Param;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface AutoProcessService {
    Result Execute(Param params, LinkedHashMap<String, Object> param_list) throws Exception;
    Result Stop(int id);
}
