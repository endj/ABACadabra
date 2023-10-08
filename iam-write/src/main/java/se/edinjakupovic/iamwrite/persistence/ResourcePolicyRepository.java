package se.edinjakupovic.iamwrite.persistence;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import se.edinjakupovic.iamwrite.service.domain.Policy;
import se.edinjakupovic.iamwrite.service.domain.ResourcePolicy;
import se.edinjakupovic.iamwrite.service.requests.CreatePolicy;

@Component
public class ResourcePolicyRepository {
    private static final Logger log = LoggerFactory.getLogger(ResourcePolicyRepository.class);
    private static final RowMapper<ResourcePolicy> MAPPER = (r, rn) -> {
        byte[] resourceIdBytes = r.getBytes(1);
        byte[] policyIdBytes = r.getBytes(2);
        var name = r.getString(3);
        var policyRule = r.getString(4);

        return new ResourcePolicy(
                Ulid.from(resourceIdBytes).toString(),
                Ulid.from(policyIdBytes).toString(),
                new Policy(
                        Ulid.from(policyIdBytes).toString(),
                        name,
                        policyRule
                )
        );
    };

    private final JdbcTemplate template;
    private final PlatformTransactionManager transactionManager;
    private static final TransactionDefinition DEFINITION = new DefaultTransactionDefinition();

    public ResourcePolicyRepository(JdbcTemplate template,
                                    PlatformTransactionManager transactionManager) {
        this.template = template;
        this.transactionManager = transactionManager;
    }

    public String create(String resourceId, CreatePolicy policy) {
        var policyId = UlidCreator.getMonotonicUlid();
        byte[] policyIdBytes = policyId.toBytes();
        byte[] resourceByteId = Ulid.from(resourceId).toBytes();

        var transaction = transactionManager.getTransaction(DEFINITION);

        try {
            int updated = template.update("""
                    INSERT INTO policies (id, name, policy_rule)
                    VALUES (?,?,?)
                    """, policyIdBytes, policy.name(), policy.policyRules());
            if (updated != 1)
                throw new DatabaseInsertException("Invalid update count " + updated);

            int updated2 = template.update("""
                    INSERT INTO resource_policy (resource_id, policy_id)
                    VALUES (?,?)
                    """, resourceByteId, policyIdBytes);
            if (updated2 != 1)
                throw new DatabaseInsertException("Invalid update count " + updated2);

            transactionManager.commit(transaction);
        } catch (Exception e) {
            log.warn("Failed to insert resource", e);
            transactionManager.rollback(transaction);
            throw new DatabaseInsertException(e.getMessage());
        }

        return policyId.toString();
    }

    public ResourcePolicy resourcePolicy(String resourceId) {
        byte[] bytes = Ulid.from(resourceId).toBytes();
        return template.queryForObject("""
                SELECT
                    rp.resource_id,
                    p.id,
                    p.name,
                    p.policy_rule
                FROM policies p
                LEFT JOIN resource_policy rp ON p.id = rp.policy_id
                WHERE rp.resource_id = ?
                """, MAPPER, bytes);
    }
}
