package com.edabip.util;

import com.edabip.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * Builds a Pageable from the common page / size / sort parameters (sort=field,asc|desc).
 * Sort fields are whitelisted so clients cannot sort by arbitrary columns.
 */
public final class PageUtil {

    private static final int MAX_SIZE = 100;

    private PageUtil() {
    }

    public static Pageable of(int page, int size, String sort, Set<String> allowedFields) {
        if (page < 0) {
            throw new BadRequestException("page must be 0 or greater");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new BadRequestException("size must be between 1 and " + MAX_SIZE);
        }
        String[] parts = (sort == null || sort.isBlank()) ? new String[]{"id"} : sort.split(",");
        String field = parts[0].trim();
        if (!allowedFields.contains(field)) {
            throw new BadRequestException("Cannot sort by '" + field + "'. Allowed: " + allowedFields);
        }
        Sort.Direction dir = Sort.Direction.ASC;
        if (parts.length > 1) {
            String d = parts[1].trim();
            if (d.equalsIgnoreCase("desc")) {
                dir = Sort.Direction.DESC;
            } else if (!d.equalsIgnoreCase("asc")) {
                throw new BadRequestException("Sort direction must be asc or desc");
            }
        }
        return PageRequest.of(page, size, Sort.by(dir, field));
    }
}
