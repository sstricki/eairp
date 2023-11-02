/*
 * Copyright 2023-2033 WanSen AI Team, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://opensource.wansenai.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.wansenai.mappers.product;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wansenai.dto.product.QueryProductExtendPriceDTO;
import com.wansenai.entities.product.ProductExtendPrice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wansenai.vo.product.ProductExtendPriceVO;

import java.util.List;

/**
 * <p>
 * 产品价格扩展 Mapper 接口
 * </p>
 */
public interface ProductExtendPriceMapper extends BaseMapper<ProductExtendPrice> {

    IPage<ProductExtendPriceVO> getProductExtendPriceList(IPage<QueryProductExtendPriceDTO> pageObject, QueryProductExtendPriceDTO queryProductExtendPriceDTO);

    ProductExtendPriceVO getProductExtendPriceByBarCode(Long barCode);
}
