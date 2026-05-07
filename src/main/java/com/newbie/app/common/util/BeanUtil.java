package com.newbie.app.common.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for copying bean properties.
 */
public final class BeanUtil {

    /**
     * Fields that are managed exclusively by the persistence/audit layer
     * and must never be overwritten during a PATCH operation.
     */
    private static final String[] ALWAYS_IGNORE = { "id", "createdAt", "updatedAt" };

    private BeanUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Copies all non-null properties from {@code source} to {@code target},
     * always ignoring {@code id}, {@code createdAt}, and {@code updatedAt}
     * to prevent accidental overwrites of audit/identity fields.
     *
     * @param source The source object (partial data coming from the request)
     * @param target The target object (existing persisted entity)
     */
    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullAndProtectedPropertyNames(source));
    }

    /**
     * Returns the names of all null properties in {@code source} plus the always-ignored fields.
     */
    private static String[] getNullAndProtectedPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] properties = src.getPropertyDescriptors();

        Set<String> ignoredProperties = new HashSet<>();

        for (java.beans.PropertyDescriptor property : properties) {
            Object value = src.getPropertyValue(property.getName());
            if (value == null) {
                ignoredProperties.add(property.getName());
            }
        }

        // Always protect identity and audit fields regardless of their value in the source
        for (String field : ALWAYS_IGNORE) {
            ignoredProperties.add(field);
        }

        return ignoredProperties.toArray(new String[0]);
    }
}