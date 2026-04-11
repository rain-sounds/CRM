package cn.cordys.crm.submission.service;

import cn.cordys.common.service.BaseService;
import cn.cordys.common.util.Translator;
import cn.cordys.crm.submission.domain.Submission;
import cn.cordys.crm.submission.dto.request.SubmissionEditRequest;
import cn.cordys.crm.submission.mapper.ExtSubmissionMapper;
import cn.cordys.crm.submission.service.impl.SubmissionServiceImpl;
import cn.cordys.crm.system.service.LogService;
import cn.cordys.crm.system.service.ModuleFormCacheService;
import cn.cordys.crm.system.service.ModuleFormService;
import cn.cordys.mybatis.BaseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubmissionServiceTest {

    @InjectMocks
    private SubmissionServiceImpl submissionService;

    @Mock
    private BaseMapper<Submission> submissionBaseMapper;

    @Mock
    private ExtSubmissionMapper extSubmissionMapper;

    @Mock
    private BaseService baseService;

    @Mock
    private SubmissionFieldService submissionFieldService;

    @Mock
    private LogService logService;

    @Mock
    private ModuleFormCacheService moduleFormCacheService;

    @Mock
    private ModuleFormService moduleFormService;

    @BeforeEach
    public void setup() {
        // mock static Translator or just lenient mocks
    }

    @Test
    public void testAdd() {
        SubmissionEditRequest request = new SubmissionEditRequest();
        request.setName("Test Submission");
        request.setProductId("P1");
        request.setOpportunityId("O1");

        Submission submission = new Submission();
        submission.setId("1");

        when(submissionBaseMapper.insert(any(Submission.class))).thenReturn(1);

        Submission result = submissionService.add(request, "U1", "ORG1");

        assertNotNull(result);
        verify(submissionBaseMapper, times(1)).insert(any(Submission.class));
    }

    @Test
    public void testDelete() {
        Submission submission = new Submission();
        submission.setId("1");
        submission.setName("Test");

        when(submissionBaseMapper.selectByPrimaryKey("1")).thenReturn(submission);

        submissionService.delete("1");

        verify(submissionBaseMapper, times(1)).deleteByPrimaryKey("1");
    }

    @Test
    public void testGet() {
        Submission submission = new Submission();
        submission.setId("1");

        when(submissionBaseMapper.selectByPrimaryKey("1")).thenReturn(submission);

        submissionService.get("1");

        verify(submissionBaseMapper, times(1)).selectByPrimaryKey("1");
    }
}