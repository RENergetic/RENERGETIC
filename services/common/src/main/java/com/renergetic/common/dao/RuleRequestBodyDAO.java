package com.renergetic.common.dao;

import com.renergetic.common.exception.InvalidNeedActionOrDefinitionException;
import com.renergetic.common.exception.InvalidRootNestedException;
import com.renergetic.common.model.Rule;
import com.renergetic.common.repository.RuleRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Getter
@Setter
public class RuleRequestBodyDAO {
    private List<RuleDAO> createUpdate;
    private List<Long> delete;
}
