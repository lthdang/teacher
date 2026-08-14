package com.teacher.security.aspect;

import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.teacher.common.exception.ForbiddenException;
import com.teacher.common.exception.UnauthorizedException;
import com.teacher.entity.Admin;
import com.teacher.entity.AdminType;
import com.teacher.security.annotation.RequirePermission;
import com.teacher.service.AdminService;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final AdminService adminService;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("Unauthorized access");
        }

        UUID adminId;
        try {
            adminId = UUID.fromString(auth.getName());
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid user identity");
        }

        Admin admin = adminService.findById(adminId)
                .orElseThrow(() -> new UnauthorizedException("Admin not found"));

        if (admin.getType() == AdminType.SUPER_ADMIN) {
            return joinPoint.proceed();
        }

        boolean hasPermission = adminService.hasPermission(adminId, requirePermission.value());
        if (!hasPermission) {
            throw new ForbiddenException("Access denied: Missing required permission '" + requirePermission.value() + "'");
        }

        return joinPoint.proceed();
    }
}
