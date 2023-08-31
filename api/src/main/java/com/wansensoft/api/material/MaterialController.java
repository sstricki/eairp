package com.wansensoft.api.material;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wansensoft.entities.material.MaterialExtend;
import com.wansensoft.entities.material.MaterialVo4Unit;
import com.wansensoft.entities.unit.Unit;
import com.wansensoft.service.depot.DepotServiceImpl;
import com.wansensoft.service.depotItem.DepotItemServiceImpl;
import com.wansensoft.service.material.MaterialServiceImpl;
import com.wansensoft.service.unit.UnitService;
import com.wansensoft.utils.BaseResponseInfo;
import com.wansensoft.utils.ErpInfo;
import com.wansensoft.utils.StringUtil;
import com.wansensoft.utils.ResponseJsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/material")
@Api(tags = {"商品管理"})
public class MaterialController {
    private Logger logger = LoggerFactory.getLogger(MaterialController.class);

    @Resource
    private MaterialServiceImpl materialServiceImpl;

    @Resource
    private DepotItemServiceImpl depotItemServiceImpl;

    @Resource
    private UnitService unitService;

    @Resource
    private DepotServiceImpl depotServiceImpl;

    @Value(value="${file.uploadType}")
    private Long fileUploadType;

    /**
     * 检查商品是否存在
     * @param id
     * @param name
     * @param model
     * @param color
     * @param standard
     * @param mfrs
     * @param otherField1
     * @param otherField2
     * @param otherField3
     * @param unit
     * @param unitId
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/checkIsExist")
    @ApiOperation(value = "检查商品是否存在")
    public String checkIsExist(@RequestParam("id") Long id, @RequestParam("name") String name,
                               @RequestParam("model") String model, @RequestParam("color") String color,
                               @RequestParam("standard") String standard, @RequestParam("mfrs") String mfrs,
                               @RequestParam("otherField1") String otherField1, @RequestParam("otherField2") String otherField2,
                               @RequestParam("otherField3") String otherField3, @RequestParam("unit") String unit,@RequestParam("unitId") Long unitId,
                               HttpServletRequest request)throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>();
        int exist = materialServiceImpl.checkIsExist(id, name, StringUtil.toNull(model), StringUtil.toNull(color),
                StringUtil.toNull(standard), StringUtil.toNull(mfrs), StringUtil.toNull(otherField1),
                StringUtil.toNull(otherField2), StringUtil.toNull(otherField3), StringUtil.toNull(unit), unitId);
        if(exist > 0) {
            objectMap.put("status", true);
        } else {
            objectMap.put("status", false);
        }
        return ResponseJsonUtil.returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }

    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/batchSetStatus")
    @ApiOperation(value = "批量设置状态-启用或者禁用")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        Boolean status = jsonObject.getBoolean("status");
        String ids = jsonObject.getString("ids");
        Map<String, Object> objectMap = new HashMap<>();
        int res = materialServiceImpl.batchSetStatus(status, ids);
        if(res > 0) {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 根据id来查询商品名称
     * @param id
     * @param request
     * @return
     */
    @GetMapping(value = "/findById")
    @ApiOperation(value = "根据id来查询商品名称")
    public BaseResponseInfo findById(@RequestParam("id") Long id, HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<MaterialVo4Unit> list = materialServiceImpl.findById(id);
            res.code = 200;
            res.data = list;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据meId来查询商品名称
     * @param meId
     * @param request
     * @return
     */
    @GetMapping(value = "/findByIdWithBarCode")
    @ApiOperation(value = "根据meId来查询商品名称")
    public BaseResponseInfo findByIdWithBarCode(@RequestParam("meId") Long meId,
                                                @RequestParam("mpList") String mpList,
                                                HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            String[] mpArr = mpList.split(",");
            MaterialVo4Unit mu = new MaterialVo4Unit();
            List<MaterialVo4Unit> list = materialServiceImpl.findByIdWithBarCode(meId);
            if(list!=null && list.size()>0) {
                mu = list.get(0);
                mu.setMaterialOther(materialServiceImpl.getMaterialOtherByParam(mpArr, mu));
            }
            res.code = 200;
            res.data = mu;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据关键词查找商品信息-条码、名称、规格、型号
     * @param q
     * @param request
     * @return
     */
    @GetMapping(value = "/getMaterialByParam")
    @ApiOperation(value = "根据关键词查找商品信息")
    public BaseResponseInfo getMaterialByParam(@RequestParam("q") String q,
                                   HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONArray arr = materialServiceImpl.getMaterialByParam(q);
            res.code = 200;
            res.data = arr;
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 查找商品信息-下拉框
     * @param mpList
     * @param request
     * @return
     */
    @GetMapping(value = "/findBySelect")
    @ApiOperation(value = "查找商品信息")
    public JSONObject findBySelect(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                  @RequestParam(value = "q", required = false) String q,
                                  @RequestParam(value = "mpList", required = false) String mpList,
                                  @RequestParam(value = "depotId", required = false) Long depotId,
                                  @RequestParam(value = "enableSerialNumber", required = false) String enableSerialNumber,
                                  @RequestParam(value = "enableBatchNumber", required = false) String enableBatchNumber,
                                  @RequestParam("page") Integer currentPage,
                                  @RequestParam("rows") Integer pageSize,
                                  HttpServletRequest request) throws Exception{
        JSONObject object = new JSONObject();
        try {
            String[] mpArr = new String[]{};
            if(StringUtil.isNotEmpty(mpList)){
                mpArr= mpList.split(",");
            }
            List<MaterialVo4Unit> dataList = materialServiceImpl.findBySelectWithBarCode(categoryId, q, enableSerialNumber,
                    enableBatchNumber, (currentPage-1)*pageSize, pageSize);
            int total = materialServiceImpl.findBySelectWithBarCodeCount(categoryId, q, enableSerialNumber,
                    enableBatchNumber);
            object.put("total", total);
            JSONArray dataArray = new JSONArray();
            //存放数据json数组
            if (null != dataList) {
                for (MaterialVo4Unit material : dataList) {
                    JSONObject item = new JSONObject();
                    item.put("id", material.getMeId()); //商品扩展表的id
                    String ratioStr = ""; //比例
                    Unit unit = new Unit();
                    if (material.getUnitId() == null) {
                        ratioStr = "";
                    } else {
                        unit = unitService.getUnit(material.getUnitId());
                        //拼接副单位的比例
                        String commodityUnit = material.getCommodityUnit();
                        if(commodityUnit.equals(unit.getBasicUnit())) {
                            ratioStr = "[基本]";
                        }
                        if(commodityUnit.equals(unit.getOtherUnit()) && unit.getRatio()!=null) {
                            ratioStr = "[" + unit.getRatio().stripTrailingZeros().toPlainString() + unit.getBasicUnit() + "]";
                        }
                        if(commodityUnit.equals(unit.getOtherUnitTwo()) && unit.getRatioTwo()!=null) {
                            ratioStr = "[" + unit.getRatioTwo().stripTrailingZeros().toPlainString() + unit.getBasicUnit() + "]";
                        }
                        if(commodityUnit.equals(unit.getOtherUnitThree()) && unit.getRatioThree()!=null) {
                            ratioStr = "[" + unit.getRatioThree().stripTrailingZeros().toPlainString() + unit.getBasicUnit() + "]";
                        }
                    }
                    item.put("mBarCode", material.getMBarCode());
                    item.put("name", material.getName());
                    item.put("categoryName", material.getCategoryName());
                    item.put("standard", material.getStandard());
                    item.put("model", material.getModel());
                    item.put("color", material.getColor());
                    item.put("unit", material.getCommodityUnit() + ratioStr);
                    item.put("sku", material.getSku());
                    item.put("enableSerialNumber", material.getEnableSerialNumber());
                    item.put("enableBatchNumber", material.getEnableBatchNumber());
                    BigDecimal stock;
                    if(StringUtil.isNotEmpty(material.getSku())){
                        stock = depotItemServiceImpl.getSkuStockByParam(depotId,material.getMeId(),null,null);
                    } else {
                        stock = depotItemServiceImpl.getCurrentStockByParam(depotId, material.getId());
                        if (material.getUnitId()!=null){
                            String commodityUnit = material.getCommodityUnit();
                            stock = unitService.parseStockByUnit(stock, unit, commodityUnit);
                        }
                    }
                    item.put("stock", stock);
                    item.put("expand", materialServiceImpl.getMaterialOtherByParam(mpArr, material));
                    item.put("imgName", material.getImgName());
                    if(fileUploadType == 2) {
                        item.put("imgSmall", "small");
                        item.put("imgLarge", "large");
                    } else {
                        item.put("imgSmall", "");
                        item.put("imgLarge", "");
                    }
                    dataArray.add(item);
                }
            }
            object.put("rows", dataArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 根据商品id查找商品信息
     * @param meId
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialByMeId")
    @ApiOperation(value = "根据商品id查找商品信息")
    public JSONObject getMaterialByMeId(@RequestParam(value = "meId", required = false) Long meId,
                                        @RequestParam("mpList") String mpList,
                                        HttpServletRequest request) throws Exception{
        JSONObject item = new JSONObject();
        try {
            String[] mpArr = mpList.split(",");
            List<MaterialVo4Unit> materialList = materialServiceImpl.getMaterialByMeId(meId);
            if(materialList!=null && materialList.size()!=1) {
                return item;
            } else if(materialList.size() == 1) {
                MaterialVo4Unit material = materialList.get(0);
                item.put("Id", material.getMeId()); //商品扩展表的id
                String ratio; //比例
                if (material.getUnitId() == null || material.getUnitId().equals("")) {
                    ratio = "";
                } else {
                    ratio = material.getUnitName();
                    ratio = ratio.substring(ratio.indexOf("("));
                }
                //名称/型号/扩展信息/包装
                String MaterialName = "";
                MaterialName = MaterialName + material.getMBarCode() + "_" + material.getName()
                        + ((material.getStandard() == null || material.getStandard().equals("")) ? "" : "(" + material.getStandard() + ")");
                String expand = materialServiceImpl.getMaterialOtherByParam(mpArr, material); //扩展信息
                MaterialName = MaterialName + expand + ((material.getUnit() == null || material.getUnit().equals("")) ? "" : "(" + material.getUnit() + ")") + ratio;
                item.put("MaterialName", MaterialName);
                item.put("name", material.getName());
                item.put("expand", expand);
                item.put("model", material.getModel());
                item.put("standard", material.getStandard());
                item.put("unit", material.getUnit() + ratio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 生成excel表格
     * @param categoryId
     * @param materialParam
     * @param color
     * @param weight
     * @param expiryNum
     * @param enabled
     * @param enableSerialNumber
     * @param enableBatchNumber
     * @param remark
     * @param mpList
     * @param request
     * @param response
     */
    @GetMapping(value = "/exportExcel")
    @ApiOperation(value = "生成excel表格")
    public void exportExcel(@RequestParam(value = "categoryId", required = false) String categoryId,
                            @RequestParam(value = "materialParam", required = false) String materialParam,
                            @RequestParam(value = "color", required = false) String color,
                            @RequestParam(value = "materialOther", required = false) String materialOther,
                            @RequestParam(value = "weight", required = false) String weight,
                            @RequestParam(value = "expiryNum", required = false) String expiryNum,
                            @RequestParam(value = "enabled", required = false) String enabled,
                            @RequestParam(value = "enableSerialNumber", required = false) String enableSerialNumber,
                            @RequestParam(value = "enableBatchNumber", required = false) String enableBatchNumber,
                            @RequestParam(value = "remark", required = false) String remark,
                            @RequestParam(value = "mpList", required = false) String mpList,
                            HttpServletRequest request, HttpServletResponse response) {
        try {
            materialServiceImpl.exportExcel(StringUtil.toNull(categoryId), StringUtil.toNull(materialParam), StringUtil.toNull(color),
                    StringUtil.toNull(materialOther), StringUtil.toNull(weight),
                    StringUtil.toNull(expiryNum), StringUtil.toNull(enabled), StringUtil.toNull(enableSerialNumber),
                    StringUtil.toNull(enableBatchNumber), StringUtil.toNull(remark), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * excel表格导入产品（含初始库存）
     * @param file
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/importExcel")
    @ApiOperation(value = "excel表格导入产品")
    public BaseResponseInfo importExcel(MultipartFile file,
                            HttpServletRequest request, HttpServletResponse response) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            res = materialServiceImpl.importExcel(file, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取商品序列号
     * @param q
     * @param currentPage
     * @param pageSize
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialEnableSerialNumberList")
    @ApiOperation(value = "获取商品序列号")
    public JSONObject getMaterialEnableSerialNumberList(
                                @RequestParam(value = "q", required = false) String q,
                                @RequestParam("page") Integer currentPage,
                                @RequestParam("rows") Integer pageSize,
                                HttpServletRequest request,
                                HttpServletResponse response)throws Exception {
        JSONObject object= new JSONObject();
        try {
            List<MaterialVo4Unit> list = materialServiceImpl.getMaterialEnableSerialNumberList(q, (currentPage-1)*pageSize, pageSize);
            Long count = materialServiceImpl.getMaterialEnableSerialNumberCount(q);
            object.put("rows", list);
            object.put("total", count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 获取最大条码
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaxBarCode")
    @ApiOperation(value = "获取最大条码")
    public BaseResponseInfo getMaxBarCode() throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        String barCode = materialServiceImpl.getMaxBarCode();
        map.put("barCode", barCode);
        res.code = 200;
        res.data = map;
        return res;
    }

    /**
     * 商品名称模糊匹配
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialNameList")
    @ApiOperation(value = "商品名称模糊匹配")
    public JSONArray getMaterialNameList() throws Exception {
        JSONArray arr = new JSONArray();
        try {
            List<String> list = materialServiceImpl.getMaterialNameList();
            for (String s : list) {
                JSONObject item = new JSONObject();
                item.put("value", s);
                item.put("text", s);
                arr.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    /**
     * 根据条码查询商品信息
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialByBarCode")
    @ApiOperation(value = "根据条码查询商品信息")
    public BaseResponseInfo getMaterialByBarCode(@RequestParam("barCode") String barCode,
                                          @RequestParam(value = "organId", required = false) Long organId,
                                          @RequestParam(value = "depotId", required = false) Long depotId,
                                          @RequestParam("mpList") String mpList,
                                          @RequestParam(required = false, value = "prefixNo") String prefixNo,
                                          HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            String[] mpArr = mpList.split(",");
            //支持序列号查询，先根据序列号查询条码，如果查不到就直接查条码
            MaterialExtend materialExtend = materialServiceImpl.getMaterialExtendBySerialNumber(barCode);
            if(materialExtend!=null && StringUtil.isNotEmpty(materialExtend.getBarCode())) {
                barCode = materialExtend.getBarCode();
            }
            List<MaterialVo4Unit> list = materialServiceImpl.getMaterialByBarCode(barCode);
            if(list!=null && list.size()>0) {
                for(MaterialVo4Unit mvo: list) {
                    mvo.setMaterialOther(materialServiceImpl.getMaterialOtherByParam(mpArr, mvo));
                    if ("LSCK".equals(prefixNo) || "LSTH".equals(prefixNo)) {
                        //零售价
                        mvo.setBillPrice(mvo.getCommodityDecimal());
                    } else if ("CGDD".equals(prefixNo) || "CGRK".equals(prefixNo) || "CGTH".equals(prefixNo)
                            || "QTRK".equals(prefixNo) || "DBCK".equals(prefixNo) || "ZZD".equals(prefixNo) || "CXD".equals(prefixNo)
                            || "PDLR".equals(prefixNo) || "PDFP".equals(prefixNo)) {
                        //采购价
                        mvo.setBillPrice(mvo.getPurchaseDecimal());
                    } else if ("XSDD".equals(prefixNo) || "XSCK".equals(prefixNo) || "XSTH".equals(prefixNo) || "QTCK".equals(prefixNo)) {
                        //销售价
                        if(organId == null) {
                            mvo.setBillPrice(mvo.getWholesaleDecimal());
                        } else {
                            //查询最后一单的销售价,实现不同的客户不同的销售价
                            BigDecimal lastUnitPrice = depotItemServiceImpl.getLastUnitPriceByParam(organId, mvo.getMeId(), prefixNo);
                            mvo.setBillPrice(lastUnitPrice!=null? lastUnitPrice : mvo.getWholesaleDecimal());
                        }
                    }
                    //仓库id
                    if (depotId == null) {
                        JSONArray depotArr = depotServiceImpl.findDepotByCurrentUser();
                        for (Object obj : depotArr) {
                            JSONObject depotObj = JSONObject.parseObject(obj.toString());
                            if (depotObj.get("isDefault") != null) {
                                Boolean isDefault = depotObj.getBoolean("isDefault");
                                if (isDefault) {
                                    Long id = depotObj.getLong("id");
                                    if (!"CGDD".equals(prefixNo) && !"XSDD".equals(prefixNo)) {
                                        //除订单之外的单据才有仓库
                                        mvo.setDepotId(id);
                                    }
                                    getStockByMaterialInfo(mvo);
                                }
                            }
                        }
                    } else {
                        mvo.setDepotId(depotId);
                        getStockByMaterialInfo(mvo);
                    }
                }
            }
            res.code = 200;
            res.data = list;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据商品信息获取库存，进行赋值
     * @param mvo
     * @throws Exception
     */
    private void getStockByMaterialInfo(MaterialVo4Unit mvo) throws Exception {
        BigDecimal stock;
        if (StringUtil.isNotEmpty(mvo.getSku())) {
            stock = depotItemServiceImpl.getSkuStockByParam(mvo.getDepotId(), mvo.getMeId(), null, null);
        } else {
            stock = depotItemServiceImpl.getCurrentStockByParam(mvo.getDepotId(), mvo.getId());
            if (mvo.getUnitId() != null) {
                Unit unit = unitService.getUnit(mvo.getUnitId());
                String commodityUnit = mvo.getCommodityUnit();
                stock = unitService.parseStockByUnit(stock, unit, commodityUnit);
            }
        }
        mvo.setStock(stock);
    }

    /**
     * 商品库存查询
     * @param currentPage
     * @param pageSize
     * @param depotIds
     * @param categoryId
     * @param materialParam
     * @param mpList
     * @param column
     * @param order
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getListWithStock")
    @ApiOperation(value = "商品库存查询")
    public BaseResponseInfo getListWithStock(@RequestParam("currentPage") Integer currentPage,
                                             @RequestParam("pageSize") Integer pageSize,
                                             @RequestParam(value = "depotIds", required = false) String depotIds,
                                             @RequestParam(value = "categoryId", required = false) Long categoryId,
                                             @RequestParam(value = "position", required = false) String position,
                                             @RequestParam("materialParam") String materialParam,
                                             @RequestParam("zeroStock") Integer zeroStock,
                                             @RequestParam("mpList") String mpList,
                                             @RequestParam("column") String column,
                                             @RequestParam("order") String order,
                                             HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<>();
        try {
            List<Long> idList = new ArrayList<>();
            List<Long> depotList = new ArrayList<>();
            if(categoryId != null){
                idList = materialServiceImpl.getListByParentId(categoryId);
            }
            if(StringUtil.isNotEmpty(depotIds)) {
                depotList = StringUtil.strToLongList(depotIds);
            } else {
                //未选择仓库时默认为当前用户有权限的仓库
                JSONArray depotArr = depotServiceImpl.findDepotByCurrentUser();
                for(Object obj: depotArr) {
                    JSONObject object = JSONObject.parseObject(obj.toString());
                    depotList.add(object.getLong("id"));
                }
            }
            List<MaterialVo4Unit> dataList = materialServiceImpl.getListWithStock(depotList, idList, StringUtil.toNull(position), StringUtil.toNull(materialParam),
                    zeroStock, StringUtil.safeSqlParse(column), StringUtil.safeSqlParse(order), (currentPage-1)*pageSize, pageSize);
            int total = materialServiceImpl.getListWithStockCount(depotList, idList, StringUtil.toNull(position), StringUtil.toNull(materialParam), zeroStock);
            MaterialVo4Unit materialVo4Unit= materialServiceImpl.getTotalStockAndPrice(depotList, idList, StringUtil.toNull(position), StringUtil.toNull(materialParam));
            map.put("total", total);
            map.put("currentStock", materialVo4Unit.getCurrentStock()!=null?materialVo4Unit.getCurrentStock():BigDecimal.ZERO);
            map.put("currentStockPrice", materialVo4Unit.getCurrentStockPrice()!=null?materialVo4Unit.getCurrentStockPrice():BigDecimal.ZERO);
            map.put("currentWeight", materialVo4Unit.getCurrentWeight()!=null?materialVo4Unit.getCurrentWeight():BigDecimal.ZERO);
            map.put("rows", dataList);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 批量设置商品当前的实时库存（按每个仓库）
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/batchSetMaterialCurrentStock")
    @ApiOperation(value = "批量设置商品当前的实时库存（按每个仓库）")
    public String batchSetMaterialCurrentStock(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        String ids = jsonObject.getString("ids");
        Map<String, Object> objectMap = new HashMap<>();
        int res = materialServiceImpl.batchSetMaterialCurrentStock(ids);
        if(res > 0) {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 批量更新商品信息
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/batchUpdate")
    @ApiOperation(value = "批量更新商品信息")
    public String batchUpdate(@RequestBody JSONObject jsonObject,
                              HttpServletRequest request)throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int res = materialServiceImpl.batchUpdate(jsonObject);
        if(res > 0) {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }
}
