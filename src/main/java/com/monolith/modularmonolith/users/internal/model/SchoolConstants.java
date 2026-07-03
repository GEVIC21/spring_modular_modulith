package com.monolith.modularmonolith.users.internal.model;

public final class SchoolConstants {
    private SchoolConstants() {}

    // Rôles existants...
    public static final String ROLE_SUPERADMIN = "ROLE_SUPERADMIN";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_ENSEIGNANT = "ROLE_ENSEIGNANT";
    public static final String ROLE_ELEVE = "ROLE_ELEVE";

    // Permissions existantes...
    public static final String USER_CREATE = "user:create";
    public static final String USER_READ = "user:read";
    public static final String USER_UPDATE = "user:update";
    public static final String USER_DELETE = "user:delete";

    public static final String STUDENT_CREATE = "student:create";
    public static final String STUDENT_READ = "student:read";
    public static final String STUDENT_UPDATE = "student:update";
    public static final String STUDENT_DELETE = "student:delete";
    public static final String STUDENT_GRADES_READ = "student:grades:read";
    public static final String STUDENT_GRADES_WRITE = "student:grades:write";
    public static final String STUDENT_ATTENDANCE_READ = "student:attendance:read";
    public static final String STUDENT_ATTENDANCE_WRITE = "student:attendance:write";

    public static final String TEACHER_CREATE = "teacher:create";
    public static final String TEACHER_READ = "teacher:read";
    public static final String TEACHER_UPDATE = "teacher:update";
    public static final String TEACHER_DELETE = "teacher:delete";
    public static final String TEACHER_COURSES_MANAGE = "teacher:courses:manage";
    public static final String TEACHER_GRADES_MANAGE = "teacher:grades:manage";

    public static final String COURSE_CREATE = "course:create";
    public static final String COURSE_READ = "course:read";
    public static final String COURSE_UPDATE = "course:update";
    public static final String COURSE_DELETE = "course:delete";

    public static final String CLASS_MANAGE = "class:manage";

    public static final String FINANCE_READ = "finance:read";
    public static final String FINANCE_WRITE = "finance:write";

    public static final String REPORTS_READ = "reports:read";
    public static final String REPORTS_WRITE = "reports:write";

    public static final String SETTINGS_MANAGE = "settings:manage";

    public static final String ANNOUNCEMENT_CREATE = "announcement:create";
    public static final String ANNOUNCEMENT_READ = "announcement:read";

    public static final String PROFILE_READ = "profile:read";
    public static final String PROFILE_WRITE = "profile:write";

    // NOUVELLES PERMISSIONS — Module Cours/Classes/Emploi du temps
    public static final String SCHEDULE_CREATE = "schedule:create";
    public static final String SCHEDULE_READ = "schedule:read";
    public static final String SCHEDULE_UPDATE = "schedule:update";
    public static final String SCHEDULE_DELETE = "schedule:delete";
}