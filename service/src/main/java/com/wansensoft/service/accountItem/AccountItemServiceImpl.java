package com.wansensoft.service.accountItem;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansensoft.entities.account.AccountItem;
import com.wansensoft.entities.account.AccountItemExample;
import com.wansensoft.entities.user.User;
import com.wansensoft.mappers.account.AccountItemMapper;
import com.wansensoft.mappers.account.AccountItemMapperEx;
import com.wansensoft.service.depotHead.DepotHeadService;
import com.wansensoft.service.log.LogService;
import com.wansensoft.service.user.UserService;
import com.wansensoft.utils.constants.BusinessConstants;
import com.wansensoft.utils.constants.ExceptionConstants;
import com.wansensoft.plugins.exception.BusinessRunTimeException;
import com.wansensoft.plugins.exception.JshException;
import com.wansensoft.utils.StringUtil;
import com.wansensoft.vo.AccountItemVo4List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class AccountItemServiceImpl extends ServiceImpl<AccountItemMapper, AccountItem> implements AccountItemService{
    private Logger logger = LoggerFactory.getLogger(AccountItemServiceImpl.class);

    private final AccountItemMapper accountItemMapper;
    private final AccountItemMapperEx accountItemMapperEx;
    private final LogService logService;
    private final UserService userService;
    private final DepotHeadService depotHeadService;

    public AccountItemServiceImpl(AccountItemMapper accountItemMapper, AccountItemMapperEx accountItemMapperEx, LogService logService, UserService userService, DepotHeadService depotHeadService) {
        this.accountItemMapper = accountItemMapper;
        this.accountItemMapperEx = accountItemMapperEx;
        this.logService = logService;
        this.userService = userService;
        this.depotHeadService = depotHeadService;
    }

    public AccountItem getAccountItem(long id) {
        AccountItem result=null;
        try{
            result=accountItemMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<AccountItem> getAccountItem() {
        AccountItemExample example = new AccountItemExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<AccountItem> list=null;
        try{
            list=accountItemMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<AccountItem> select(String name, Integer type, String remark, int offset, int rows) {
        List<AccountItem> list=null;
        try{
            list = accountItemMapperEx.selectByConditionAccountItem(name, type, remark, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countAccountItem(String name, Integer type, String remark) {
        Long result=null;
        try{
            result = accountItemMapperEx.countsByAccountItem(name, type, remark);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertAccountItem(JSONObject obj, HttpServletRequest request) {
        AccountItem accountItem = JSONObject.parseObject(obj.toJSONString(), AccountItem.class);
        int result=0;
        try{
            result = accountItemMapper.insertSelective(accountItem);
            logService.insertLog("财务明细", BusinessConstants.LOG_OPERATION_TYPE_ADD, request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateAccountItem(JSONObject obj, HttpServletRequest request) {
        AccountItem accountItem = JSONObject.parseObject(obj.toJSONString(), AccountItem.class);
        int result=0;
        try{
            result = accountItemMapper.updateByPrimaryKeySelective(accountItem);
            logService.insertLog("财务明细",
                    BusinessConstants.LOG_OPERATION_TYPE_EDIT + accountItem.getId(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteAccountItem(Long id, HttpServletRequest request) {
        int result=0;
        try{
            result = accountItemMapper.deleteByPrimaryKey(id);
            logService.insertLog("财务明细",
                    BusinessConstants.LOG_OPERATION_TYPE_DELETE + id, request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteAccountItem(String ids, HttpServletRequest request) {
        List<Long> idList = StringUtil.strToLongList(ids);
        AccountItemExample example = new AccountItemExample();
        example.createCriteria().andIdIn(idList);
        int result=0;
        try{
            result = accountItemMapper.deleteByExample(example);
            logService.insertLog("财务明细", "批量删除,id集:" + ids, request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public int checkIsNameExist(Long id, String name) {
        AccountItemExample example = new AccountItemExample();
        example.createCriteria().andIdNotEqualTo(id).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<AccountItem> list = null;
        try{
            list = accountItemMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertAccountItemWithObj(AccountItem accountItem) {
        int result=0;
        try{
            result = accountItemMapper.insertSelective(accountItem);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateAccountItemWithObj(AccountItem accountItem) {
        int result=0;
        try{
            result = accountItemMapper.updateByPrimaryKeySelective(accountItem);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public List<AccountItemVo4List> getDetailList(Long headerId) {
        List<AccountItemVo4List> list=null;
        try{
            list = accountItemMapperEx.getDetailList(headerId);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void saveDetials(String rows, Long headerId, String type, HttpServletRequest request) {
        //删除单据的明细
        deleteAccountItemHeadId(headerId);
        JSONArray rowArr = JSONArray.parseArray(rows);
        if (null != rowArr && rowArr.size()>0) {
            for (int i = 0; i < rowArr.size(); i++) {
                AccountItem accountItem = new AccountItem();
                JSONObject tempInsertedJson = JSONObject.parseObject(rowArr.getString(i));
                accountItem.setHeaderId(headerId);
                if (tempInsertedJson.get("accountId") != null && !tempInsertedJson.get("accountId").equals("")) {
                    accountItem.setAccountId(tempInsertedJson.getLong("accountId"));
                }
                if (tempInsertedJson.get("inOutItemId") != null && !tempInsertedJson.get("inOutItemId").equals("")) {
                    accountItem.setInOutItemId(tempInsertedJson.getLong("inOutItemId"));
                }
                if (tempInsertedJson.get("billNumber") != null && !tempInsertedJson.get("billNumber").equals("")) {
                    String billNo = tempInsertedJson.getString("billNumber");
                    accountItem.setBillId(depotHeadService.getDepotHead(billNo).getId());
                }
                if (tempInsertedJson.get("needDebt") != null && !tempInsertedJson.get("needDebt").equals("")) {
                    accountItem.setNeedDebt(tempInsertedJson.getBigDecimal("needDebt"));
                }
                if (tempInsertedJson.get("finishDebt") != null && !tempInsertedJson.get("finishDebt").equals("")) {
                    accountItem.setFinishDebt(tempInsertedJson.getBigDecimal("finishDebt"));
                }
                if (tempInsertedJson.get("eachAmount") != null && !tempInsertedJson.get("eachAmount").equals("")) {
                    BigDecimal eachAmount = tempInsertedJson.getBigDecimal("eachAmount");
                    if (type.equals("付款")) {
                        eachAmount = BigDecimal.ZERO.subtract(eachAmount);
                    }
                    accountItem.setEachAmount(eachAmount);
                } else {
                    accountItem.setEachAmount(BigDecimal.ZERO);
                }
                accountItem.setRemark(tempInsertedJson.getString("remark"));
                this.insertAccountItemWithObj(accountItem);
            }
        } else {
            throw new BusinessRunTimeException(ExceptionConstants.ACCOUNT_HEAD_ROW_FAILED_CODE,
                    String.format(ExceptionConstants.ACCOUNT_HEAD_ROW_FAILED_MSG));
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void deleteAccountItemHeadId(Long headerId) {
        AccountItemExample example = new AccountItemExample();
        example.createCriteria().andHeaderIdEqualTo(headerId);
        try{
            accountItemMapper.deleteByExample(example);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteAccountItemByIds(String ids) {
        logService.insertLog("财务明细",
                BusinessConstants.LOG_OPERATION_TYPE_DELETE + ids,
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        User userInfo = userService.getCurrentUser();
        String [] idArray=ids.split(",");
        int result=0;
        try{
            result = accountItemMapperEx.batchDeleteAccountItemByIds(new Date(),userInfo==null?null:userInfo.getId(),idArray);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public BigDecimal getEachAmountByBillId(Long billId) {
        return accountItemMapperEx.getEachAmountByBillId(billId).abs();
    }
}
