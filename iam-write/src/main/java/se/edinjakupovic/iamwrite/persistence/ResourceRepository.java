package se.edinjakupovic.iamwrite.persistence;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import se.edinjakupovic.iamwrite.service.domain.Attribute;
import se.edinjakupovic.iamwrite.service.domain.Resource;
import se.edinjakupovic.iamwrite.service.requests.CreateResource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class ResourceRepository {
    private static final RowMapper<Resource> MAPPER = (r, rn) -> {
        byte[] idBytes = r.getBytes(1);
        var name = r.getString(2);
        var attributes = r.getString(3);

        return new Resource(
                Ulid.from(idBytes).toString(),
                name,
                Arrays.stream(attributes.split(","))
                        .map(attribute -> {
                            String[] parts = attribute.split(":");
                            return new Attribute(parts[0], parts[1]);
                        }).toList()
        );
    };
    private static final Logger log = LoggerFactory.getLogger(ResourceRepository.class);
    private static final TransactionDefinition DEFINITION = new DefaultTransactionDefinition();

    private final JdbcTemplate template;
    private final PlatformTransactionManager transactionManager;

    public ResourceRepository(JdbcTemplate template,
                              PlatformTransactionManager transactionManager) {
        this.template = template;
        this.transactionManager = transactionManager;
    }

    public Collection<Resource> getAll() {
        return template
                .query("""
                        SELECT
                            r.id,
                            r.name,
                            GROUP_CONCAT(ra.attribute_key, ':', ra.attribute_value SEPARATOR ',') AS attributes
                        FROM resources r
                        LEFT JOIN resource_attributes ra ON r.id = ra.resource_id
                        GROUP BY r.id, r.name;
                        """, MAPPER);
    }

    @SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
    public Resource getById(String resourceId) {
        byte[] bytes = Ulid.from(resourceId).toBytes();
        return template.queryForObject("""
                SELECT
                    r.id,
                    r.name,
                    GROUP_CONCAT(ra.attribute_key, ':', ra.attribute_value SEPARATOR ',') AS attributes
                FROM resources r
                LEFT JOIN resource_attributes ra ON r.id = ra.resource_id
                WHERE r.id = ?
                GROUP BY r.id, r.name;
                """, MAPPER, bytes);
    }

    public String create(CreateResource request) {
        var id = UlidCreator.getMonotonicUlid();
        byte[] idBytes = id.toBytes();

        var transaction = transactionManager.getTransaction(DEFINITION);

        try {
            int updated = template.update("""
                    INSERT INTO resources (id, name)
                    VALUES (?,?)
                    """, idBytes, request.name());
            if (updated != 1)
                throw new DatabaseInsertException("Invalid update count " + updated);

            List<Attribute> attributes = request.attributes();
            int[] updates = template.batchUpdate("""
                    INSERT INTO resource_attributes (resource_id, attribute_key, attribute_value)
                    VALUES (?,?,?)
                    """, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Attribute attribute = attributes.get(i);
                    ps.setBytes(1, idBytes);
                    ps.setString(2, attribute.key());
                    ps.setString(3, attribute.value());
                }

                @Override
                public int getBatchSize() {
                    return attributes.size();
                }
            });
            int sum = Arrays.stream(updates).sum();
            if (sum != request.attributes().size())
                throw new DatabaseInsertException("Invalid update count " + sum);

            transactionManager.commit(transaction);
        } catch (Exception e) {
            log.warn("Failed to insert resource", e);
            transactionManager.rollback(transaction);
            throw new DatabaseInsertException(e.getMessage());
        }


        return id.toString();
    }

    public List<String> getAllResourceIds() {
        return template.query("""
                SELECT id from resources
                """, (r, rn) -> {
            byte[] bytes = r.getBytes(1);
            return Ulid.from(bytes).toString();
        });
    }

    public List<String> resourcesWithPolicies() {
        return template.query("""
                SELECT resource_id from resource_policy
                """, (r, rn) -> {
            byte[] bytes = r.getBytes(1);
            return Ulid.from(bytes).toString();
        });
    }
}
