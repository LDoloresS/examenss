package com.codigo.examen_ss.aggregates.constants;

public class Constants {
    public static final String USU_CREA = "LDOLORESS";
    public static final Integer OK_DNI_CODE = 2000;
    public static final String OK_DNI_MESS = "REGISTRADO XVR!";
    public static final Integer ERROR_DNI_CODE = 2004;
    public static final String ERROR_DNI_MESS = "ERROR CON EL DNI";
    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_INACTIVE = 0;
    public static final Integer ERROR_CODE_LIST_EMPTY = 2009;
    public static final String ERROR_MESS_LIST_EMPTY = "NO HAY REGISTROS!!";
    public static final Integer ERROR_CODE_UPD = 2008;
    public static final String ERROR_MESS_UPD = "ERROR AL ACTUALIZAR";
    public static final Integer ERROR_CODE_DEL = 2007;
    public static final String ERROR_MESS_DEL = "ERROR AL ELIMINAR";
    public static final String REDIS_KEY_API_PERSON = "MS:EXAMENSS:";
    public static final Integer REDIS_EXP = 5;
    public static final String CLAVE_AccountNonExpired = "isAccountNonExpired";
    public static final String CLAVE_AccountNonLocked = "isAccountNonLocked";
    public static final String CLAVE_CredentialsNonExpired = "isCredentialsNonExpired";
    public static final String CLAVE_Enabled = "isEnabled";
    public static final Boolean ESTADO_ACTIVO = true;
    public static final Integer ERROR_CODE_LOGIN = 2005;
    public static final String ERROR_MESS_LOGIN = "ERROR AL LOGUEARSE";
    public static final String ENPOINTS_PERMIT = "/api/examensbss_lin/authentication/v1/**";
    public static final String ENPOINTS_USER = "/api/examensbss_lin/user/v1/**";
    public static final String ENPOINTS_ADMIN = "/api/examensbss_lin/admin/v1/**";
    public static final String ENPOINTS_DEBUG = "/api/examensbss_lin/usuarios/v1/**";
}
