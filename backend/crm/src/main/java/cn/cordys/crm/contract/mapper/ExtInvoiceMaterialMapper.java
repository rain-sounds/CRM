package cn.cordys.crm.contract.mapper;

import cn.cordys.crm.contract.dto.request.InvoiceMaterialPageRequest;
import cn.cordys.crm.contract.dto.response.InvoiceMaterialListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtInvoiceMaterialMapper {

    List<InvoiceMaterialListResponse> list(@Param("request") InvoiceMaterialPageRequest request, @Param("orgId") String orgId);

    Integer getMaxSequence(@Param("orgId") String orgId);
}
