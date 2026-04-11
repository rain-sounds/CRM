package cn.cordys.crm.submission.domain;

import cn.cordys.common.domain.BaseResourceField;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "submission_field")
public class SubmissionField extends BaseResourceField {
}