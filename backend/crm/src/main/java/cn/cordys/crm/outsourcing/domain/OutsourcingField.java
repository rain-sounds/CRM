package cn.cordys.crm.outsourcing.domain;

import cn.cordys.common.domain.BaseResourceField;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 外包自定义属性
 *
 * @author ls
 * @date 2026-06-11
 */
@Data
@Table(name = "outsourcing_field")
public class OutsourcingField extends BaseResourceField {
}
