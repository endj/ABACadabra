package se.edinjakupovic.iamwrite.persistence;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import se.edinjakupovic.iamwrite.service.domain.Subject;
import se.edinjakupovic.iamwrite.service.requests.CreateSubject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static se.edinjakupovic.iamwrite.service.domain.conditions.AttributeParser.parseAttributes;

@Component
public class SubjectRepository {

    private static final Logger log = LoggerFactory.getLogger(SubjectRepository.class);
    private static final RowMapper<Subject> MAPPER = (r, rn) -> {
        byte[] idBytes = r.getBytes(1);
        var email = r.getString(2);
        var firstName = r.getString(3);
        var lastName = r.getString(4);
        var attributes = r.getString(5);

        return new Subject(
                Ulid.from(idBytes).toString(),
                email,
                firstName,
                lastName,
                parseAttributes(attributes)
        );
    };
    private static final TransactionDefinition DEFINITION = new DefaultTransactionDefinition();
    private final JdbcTemplate template;
    private final PlatformTransactionManager transactionManager;

    public SubjectRepository(JdbcTemplate template,
                             PlatformTransactionManager transactionManager) {
        this.template = template;
        this.transactionManager = transactionManager;
    }

    public Collection<Subject> getAll() {
        return template.query("""
                SELECT
                    s.id,
                    s.subject_email,
                    s.first_name,
                    s.last_name,
                    GROUP_CONCAT(sa.attribute_key, ':', sa.attribute_value SEPARATOR ',') AS attributes
                FROM subjects s
                LEFT JOIN subject_attributes sa ON s.id = sa.subject_id
                GROUP BY s.id, s.subject_email, s.first_name, s.last_name;
                """, MAPPER);
    }

    @SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
    @Nullable
    public Subject getById(String subjectId) {
        byte[] bytes = Ulid.from(subjectId).toBytes();
        return template.queryForObject("""
                SELECT
                    s.id,
                    s.subject_email,
                    s.first_name,
                    s.last_name,
                    GROUP_CONCAT(sa.attribute_key, ':', sa.attribute_value SEPARATOR ',') AS attributes
                FROM subjects s
                LEFT JOIN subject_attributes sa ON s.id = sa.subject_id
                WHERE s.id = ?
                GROUP BY s.id, s.subject_email, s.first_name, s.last_name;
                """, MAPPER, bytes);
    }


    public String create(CreateSubject request) {
        var id = UlidCreator.getMonotonicUlid();
        byte[] idBytes = id.toBytes();

        var transaction = transactionManager.getTransaction(DEFINITION);

        try {
            int updated = template.update("""
                    INSERT INTO subjects (id, subject_email, first_name, last_name)
                    VALUES (?,?,?,?)
                    """, idBytes, request.email(), request.firstName(), request.lastName());
            if (updated != 1)
                throw new DatabaseInsertException("Invalid update count " + updated);

            List<Attribute> attributes = request.attributes();
            int[] updates = template.batchUpdate("""
                    INSERT INTO subject_attributes (subject_id, attribute_key, attribute_value)
                    VALUES (?,?,?)
                    """, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
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
            log.warn("Failed to insert subject ", e);
            transactionManager.rollback(transaction);
            throw new DatabaseInsertException(getAll().toString());
        }

        return id.toString();
    }

    public List<String> getAllSubjectIds() {
        return template.query("""
                SELECT id from subjects
                """, (r, rn) -> {
            byte[] bytes = r.getBytes(1);
            return Ulid.from(bytes).toString();
        });
    }
}
