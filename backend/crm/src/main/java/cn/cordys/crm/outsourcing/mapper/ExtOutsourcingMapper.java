package cn.cordys.crm.outsourcing.mapper;

import cn.cordys.common.dto.DeptDataPermissionDTO;
import cn.cordys.crm.outsourcing.dto.request.OutsourcingPageRequest;
import cn.cordys.crm.outsourcing.dto.response.OutsourcingListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 外包Mapper接口
 *
 * @author ls
 * @date 2026-06-11
 */
public interface ExtOutsourcingMapper {

    List<OutsourcingListResponse> list(@Param("request") OutsourcingPageRequest request,
                                       @Param("userId") String userId,
                                       @Param("orgId") String orgId,
                                       @Param("dataPermission") DeptDataPermissionDTO deptDataPermission);

    List<OutsourcingListResponse> getListByIds(@Param("ids") List<String> ids,
                                               @Param("userId") String userId,
                                               @Param("orgId") String orgId,
                                               @Param("dataPermission") DeptDataPermissionDTO deptDataPermission);
}
