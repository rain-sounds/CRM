package cn.cordys.crm.contract;

import cn.cordys.common.constants.PermissionConstants;
import cn.cordys.common.pager.Pager;
import cn.cordys.crm.base.BaseTest;
import cn.cordys.crm.contract.domain.InvoiceMaterial;
import cn.cordys.crm.contract.dto.request.InvoiceMaterialAddRequest;
import cn.cordys.crm.contract.dto.request.InvoiceMaterialPageRequest;
import cn.cordys.crm.contract.dto.request.InvoiceMaterialUpdateRequest;
import cn.cordys.crm.contract.dto.response.InvoiceMaterialListResponse;
import cn.cordys.mybatis.BaseMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InvoiceMaterialControllerTests extends BaseTest {

    private static final String BASE_PATH = "/contract/invoice-material/";

    private static InvoiceMaterial addInvoiceMaterial;

    @Resource
    private BaseMapper<InvoiceMaterial> invoiceMaterialMapper;

    @Override
    protected String getBasePath() {
        return BASE_PATH;
    }

    @Test
    @Order(1)
    void testAdd() throws Exception {
        InvoiceMaterialAddRequest request = new InvoiceMaterialAddRequest();
        request.setHospitalName("测试医院_" + System.currentTimeMillis());
        request.setInvoice("是");
        request.setVerificationProof("否");
        request.setSampleMailing("是");
        request.setSamplePhoto("否");
        request.setReport("是");
        request.setOutboundOrder("否");
        request.setContract("是");
        request.setPlatform("喀斯码");
        request.setOtherMaterials("测试其他资料");

        MvcResult mvcResult = this.requestPostWithOkAndReturn(DEFAULT_ADD, request);
        InvoiceMaterial resultData = getResultData(mvcResult, InvoiceMaterial.class);
        addInvoiceMaterial = invoiceMaterialMapper.selectByPrimaryKey(resultData.getId());

        // 校验数据写入
        Assertions.assertNotNull(addInvoiceMaterial);
        Assertions.assertEquals(request.getHospitalName(), addInvoiceMaterial.getHospitalName());
        Assertions.assertEquals(request.getInvoice(), addInvoiceMaterial.getInvoice());
        Assertions.assertEquals(request.getVerificationProof(), addInvoiceMaterial.getVerificationProof());
        Assertions.assertEquals(request.getSampleMailing(), addInvoiceMaterial.getSampleMailing());
        Assertions.assertEquals(request.getSamplePhoto(), addInvoiceMaterial.getSamplePhoto());
        Assertions.assertEquals(request.getReport(), addInvoiceMaterial.getReport());
        Assertions.assertEquals(request.getOutboundOrder(), addInvoiceMaterial.getOutboundOrder());
        Assertions.assertEquals(request.getContract(), addInvoiceMaterial.getContract());
        Assertions.assertEquals(request.getPlatform(), addInvoiceMaterial.getPlatform());
        Assertions.assertEquals(request.getOtherMaterials(), addInvoiceMaterial.getOtherMaterials());
        Assertions.assertNotNull(addInvoiceMaterial.getSequence());
        Assertions.assertTrue(addInvoiceMaterial.getSequence() > 0);

        // 校验权限
        requestPostPermissionTest(PermissionConstants.CONTRACT_INVOICE_MATERIAL_ADD, DEFAULT_ADD, request);
    }

    @Test
    @Order(2)
    void testGet() throws Exception {
        MvcResult mvcResult = this.requestGetWithOkAndReturn(DEFAULT_GET, addInvoiceMaterial.getId());
        InvoiceMaterialListResponse resultData = getResultData(mvcResult, InvoiceMaterialListResponse.class);

        Assertions.assertNotNull(resultData);
        Assertions.assertEquals(addInvoiceMaterial.getId(), resultData.getId());
        Assertions.assertEquals(addInvoiceMaterial.getHospitalName(), resultData.getHospitalName());

        // 校验权限
        requestGetPermissionTest(PermissionConstants.CONTRACT_INVOICE_MATERIAL_READ, DEFAULT_GET, addInvoiceMaterial.getId());
    }

    @Test
    @Order(3)
    void testPage() throws Exception {
        InvoiceMaterialPageRequest request = new InvoiceMaterialPageRequest();
        request.setCurrent(1);
        request.setPageSize(10);

        MvcResult mvcResult = this.requestPostWithOkAndReturn(DEFAULT_PAGE, request);
        Pager<List<InvoiceMaterialListResponse>> pageResult = getPageResult(mvcResult, InvoiceMaterialListResponse.class);

        Assertions.assertNotNull(pageResult);
        Assertions.assertTrue(pageResult.getTotal() > 0);
        Assertions.assertFalse(pageResult.getList().isEmpty());

        // 校验权限
        requestPostPermissionTest(PermissionConstants.CONTRACT_INVOICE_MATERIAL_READ, DEFAULT_PAGE, request);
    }

    @Test
    @Order(4)
    void testUpdate() throws Exception {
        InvoiceMaterialUpdateRequest request = new InvoiceMaterialUpdateRequest();
        request.setId(addInvoiceMaterial.getId());
        request.setHospitalName("更新后的医院_" + System.currentTimeMillis());
        request.setInvoice("否");
        request.setVerificationProof("是");
        request.setSampleMailing("否");
        request.setSamplePhoto("是");
        request.setReport("否");
        request.setOutboundOrder("是");
        request.setContract("否");
        request.setPlatform("锐竞");
        request.setOtherMaterials("更新后的其他资料");

        this.requestPostWithOk(DEFAULT_UPDATE, request);

        InvoiceMaterial updated = invoiceMaterialMapper.selectByPrimaryKey(addInvoiceMaterial.getId());
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(request.getHospitalName(), updated.getHospitalName());
        Assertions.assertEquals(request.getInvoice(), updated.getInvoice());
        Assertions.assertEquals(request.getPlatform(), updated.getPlatform());
        Assertions.assertEquals(request.getOtherMaterials(), updated.getOtherMaterials());

        // 校验权限
        requestPostPermissionTest(PermissionConstants.CONTRACT_INVOICE_MATERIAL_UPDATE, DEFAULT_UPDATE, request);
    }

    @Test
    @Order(5)
    void testDelete() throws Exception {
        this.requestGetWithOk(DEFAULT_DELETE, addInvoiceMaterial.getId());
        InvoiceMaterial deleted = invoiceMaterialMapper.selectByPrimaryKey(addInvoiceMaterial.getId());
        Assertions.assertNull(deleted);

        // 校验权限
        requestGetPermissionTest(PermissionConstants.CONTRACT_INVOICE_MATERIAL_DELETE, DEFAULT_DELETE, "nonexistent-id");
    }
}
