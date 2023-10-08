package se.edinjakupovic.iamwrite.persistence;

import com.github.f4b6a3.ulid.Ulid;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import static se.edinjakupovic.iamwrite.service.domain.conditions.AttributeParser.parseAttributes;

@Component
public class AuthorizationRepository {

    private static final RowMapper<AuthorizationContext> MAPPER = (r, rn) -> {
        String subjectAttributes = r.getString(1);
        String resourceAttributes = r.getString(2);
        String policyRule = r.getString(3);
        return new AuthorizationContext(
                parseAttributes(subjectAttributes),
                parseAttributes(resourceAttributes),
                policyRule
        );
    };
    private final JdbcTemplate template;

    public AuthorizationRepository(JdbcTemplate template) {
        this.template = template;
    }

    public AuthorizationContext fetchAuthContext(String subjectId, String resourceId) {
        byte[] subjectIdBytes = Ulid.from(subjectId).toBytes();
        byte[] resourceIdBytes = Ulid.from(resourceId).toBytes();
        return template.queryForObject("""
                SELECT
                    GROUP_CONCAT(DISTINCT sa.attribute_key, ':', sa.attribute_value SEPARATOR ',') AS sa_attributes,
                    GROUP_CONCAT(DISTINCT ra.attribute_key, ':', ra.attribute_value SEPARATOR ',') AS ra_attributes,
                    policies.policy_rule
                FROM
                    resource_attributes ra
                JOIN subject_attributes sa ON sa.subject_id = ?
                JOIN resource_policy ON ra.resource_id = resource_policy.resource_id
                JOIN policies ON policies.id = resource_policy.policy_id
                WHERE
                    resource_policy.resource_id = ?
                GROUP BY sa.subject_id, ra.resource_id, policies.policy_rule;
                """, MAPPER, subjectIdBytes, resourceIdBytes);
    }
}
