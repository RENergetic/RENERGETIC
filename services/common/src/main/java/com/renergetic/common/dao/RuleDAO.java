package com.renergetic.common.dao;

import com.renergetic.common.exception.InvalidNeedActionOrDefinitionException;
import com.renergetic.common.exception.InvalidRootNestedException;
import com.renergetic.common.model.Rule;
import com.renergetic.common.repository.RuleRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class RuleDAO {
    @Autowired
    private RuleRepository ruleRepository;

    private Long id;
    private RuleDefinitionDAO ruleDefinition;
    private RuleActionDAO ruleAction;
    private RuleDAO positiveRule;
    private RuleDAO negativeRule;
    private boolean active;

    public Rule mapToEntity(){
        if(ruleRepository.existsByIdAndByRootTrue(getId()))
            throw new InvalidRootNestedException();
        return (mapToEntity(true));
    }

    private Rule mapToEntity(boolean root){
        if((getRuleDefinition() == null && getRuleAction() == null) ||
                (getRuleDefinition() != null && getRuleAction() != null))
            throw new InvalidNeedActionOrDefinitionException();

        Rule rule = new Rule();
        rule.setId(getId());
        if(getRuleDefinition() != null)
            rule.setRuleDefinition(getRuleDefinition().mapToEntity());
        if(getRuleAction() != null)
            rule.setRuleAction(getRuleAction().mapToEntity());
        if(getPositiveRule() != null)
            rule.setPositiveRule(getPositiveRule().mapToEntity(false));
        if(getNegativeRule() != null)
            rule.setNegativeRule(getNegativeRule().mapToEntity(false));
        rule.setActive(isActive());
        rule.setRoot(root);
        return rule;
    }

    public static RuleDAO fromEntity(Rule entity){
        return fromEntity(entity, true);
    }

    private static RuleDAO fromEntity(Rule entity, boolean allowRoot){
        if(!allowRoot && entity.getRoot())
            throw new InvalidRootNestedException();

        RuleDAO dao = new RuleDAO();
        dao.setId(entity.getId());
        if(entity.getRuleDefinition() != null)
            dao.setRuleDefinition(RuleDefinitionDAO.fromEntity(entity.getRuleDefinition()));
        if(entity.getRuleAction() != null)
            dao.setRuleAction(RuleActionDAO.fromEntity(entity.getRuleAction()));
        if(entity.getPositiveRule() != null)
            dao.setPositiveRule(RuleDAO.fromEntity(entity.getPositiveRule(), false));
        if(entity.getNegativeRule() != null)
            dao.setNegativeRule(RuleDAO.fromEntity(entity.getNegativeRule(), false));
        dao.setActive(entity.getActive());
        return dao;
    }
}
