package com.wansensoft.service.materialProperty;

import com.alibaba.fastjson.JSONObject;
import com.wansensoft.entities.material.MaterialProperty;
import com.wansensoft.entities.material.MaterialPropertyExample;
import com.wansensoft.entities.user.User;
import com.wansensoft.mappers.material.MaterialPropertyMapper;
import com.wansensoft.mappers.material.MaterialPropertyMapperEx;
import com.wansensoft.service.log.LogServiceImpl;
import com.wansensoft.service.user.UserServiceImpl;
import com.wansensoft.utils.constants.BusinessConstants;
import com.wansensoft.plugins.exception.JshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class MaterialPropertyService {
    private Logger logger = LoggerFactory.getLogger(MaterialPropertyService.class);

    private final MaterialPropertyMapper materialPropertyMapper;
    private final MaterialPropertyMapperEx materialPropertyMapperEx;
    private final UserServiceImpl userServiceImpl;
    private final LogServiceImpl logServiceImpl;

    public MaterialPropertyService(MaterialPropertyMapper materialPropertyMapper, MaterialPropertyMapperEx materialPropertyMapperEx, UserServiceImpl userServiceImpl, LogServiceImpl logServiceImpl) {
        this.materialPropertyMapper = materialPropertyMapper;
        this.materialPropertyMapperEx = materialPropertyMapperEx;
        this.userServiceImpl = userServiceImpl;
        this.logServiceImpl = logServiceImpl;
    }

    public MaterialProperty getMaterialProperty(long id)throws Exception {
        MaterialProperty result=null;
        try{
            result=materialPropertyMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<MaterialProperty> getMaterialProperty()throws Exception {
        MaterialPropertyExample example = new MaterialPropertyExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<MaterialProperty>  list=null;
        try{
            list=materialPropertyMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<MaterialProperty> select(String name, int offset, int rows)throws Exception {
        List<MaterialProperty>  list=null;
        try{
            if(BusinessConstants.DEFAULT_MANAGER.equals(userServiceImpl.getCurrentUser().getLoginName())) {
                list = materialPropertyMapperEx.selectByConditionMaterialProperty(name, offset, rows);
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countMaterialProperty(String name)throws Exception {
        Long  result=null;
        try{
            if(BusinessConstants.DEFAULT_MANAGER.equals(userServiceImpl.getCurrentUser().getLoginName())) {
                result = materialPropertyMapperEx.countsByMaterialProperty(name);
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertMaterialProperty(JSONObject obj, HttpServletRequest request)throws Exception {
        MaterialProperty materialProperty = JSONObject.parseObject(obj.toJSONString(), MaterialProperty.class);
        int  result=0;
        try{
            if(BusinessConstants.DEFAULT_MANAGER.equals(userServiceImpl.getCurrentUser().getLoginName())) {
                result = materialPropertyMapper.insertSelective(materialProperty);
                logServiceImpl.insertLog("商品属性",
                        new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(materialProperty.getNativeName()).toString(), request);
            }
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateMaterialProperty(JSONObject obj, HttpServletRequest request)throws Exception {
        MaterialProperty materialProperty = JSONObject.parseObject(obj.toJSONString(), MaterialProperty.class);
        int  result=0;
        try{
            if(BusinessConstants.DEFAULT_MANAGER.equals(userServiceImpl.getCurrentUser().getLoginName())) {
                result = materialPropertyMapper.updateByPrimaryKeySelective(materialProperty);
                logServiceImpl.insertLog("商品属性",
                        new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(materialProperty.getNativeName()).toString(), request);
            }
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteMaterialProperty(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteMaterialPropertyByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteMaterialProperty(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteMaterialPropertyByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteMaterialPropertyByIds(String ids) throws Exception{
        User userInfo= userServiceImpl.getCurrentUser();
        String [] idArray=ids.split(",");
        int  result=0;
        try{
            if(BusinessConstants.DEFAULT_MANAGER.equals(userServiceImpl.getCurrentUser().getLoginName())) {
                result = materialPropertyMapperEx.batchDeleteMaterialPropertyByIds(new Date(), userInfo == null ? null : userInfo.getId(), idArray);
                logServiceImpl.insertLog("商品属性",
                        new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_DELETE).append(ids).toString(),
                        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
            }
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public int checkIsNameExist(Long id, String name)throws Exception {
        return 0;
    }
}
