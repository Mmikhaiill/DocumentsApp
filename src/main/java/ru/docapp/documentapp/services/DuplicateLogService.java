package ru.docapp.documentapp.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.docapp.documentapp.dto.DuplicateLogEntry;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DuplicateLogService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logDuplicate(DuplicateLogEntry entry) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO duplicate_log (entity_type, duplicate_value, context, timestamp) VALUES (?, ?, ?, ?)",
                    entry.getEntityType(),
                    entry.getDuplicateValue(),
                    entry.getContext(),
                    LocalDateTime.now()
            );
            log.info("Duplicate logged: {} = {}", entry.getEntityType(), entry.getDuplicateValue());
        } catch (DataAccessException e) {
            log.error("Failed to log duplicate entry: {}", entry, e);
        }
    }
}